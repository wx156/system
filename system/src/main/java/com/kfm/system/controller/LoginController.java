package com.kfm.system.controller;


import com.kfm.system.bean.IPInfo;
import com.kfm.system.domain.User;
import com.kfm.system.ex.ServiceException;
import com.kfm.system.service.impl.EmailServiceImpl;
import com.kfm.system.service.impl.UserServiceImpl;
import com.kfm.system.util.*;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.utils.CaptchaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class LoginController {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private EmailServiceImpl emailService;
    @Autowired
    private IPInfo ipInfo;
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping({"/", "/login"})
    public ModelAndView login() {
        return new ModelAndView("login/login");
    }

    @GetMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GifCaptcha gifCaptcha = new GifCaptcha(130, 40, 4);
        CaptchaUtil.out(gifCaptcha, request, response);
    }

    @PostMapping("/login")
    public Resp login(User user,@RequestParam(required = false) String captcha,@RequestParam(required = false) String verification, HttpServletRequest request) {
        // 参数校验,判断User所有字段是否为空
        if (ObjectUtils.isEmpty(user.getUsername()) && ObjectUtils.isEmpty(user.getPassword())
                && ObjectUtils.isEmpty(user.getEmail()) && ObjectUtils.isEmpty(user.getPhone())) {
            return Resp.error("参数校验失败");
        }

        if (StringUtils.hasText(captcha)){
            if (!CaptchaUtil.ver(captcha, request)) {
                // 清除验证码
                CaptchaUtil.clear(request);
                return Resp.error("验证码错误");
            }
        }
        if (!StringUtils.hasText(verification) && StringUtils.hasText(user.getEmail())){
            return Resp.error("验证码错误");
        }
        // 验证验证码是否过期

        if (StringUtils.hasText(verification) && StringUtils.hasText(user.getEmail())){
            if (redisTemplate.hasKey(Constant.LOGIN_CODE + user.getEmail()) && redisTemplate.opsForValue().get(Constant.LOGIN_CODE + user.getEmail()) == null){
                return Resp.error("验证码已过期");
            }
            String code = (String) redisTemplate.opsForValue().get(Constant.LOGIN_CODE + user.getEmail());
            if (ObjectUtils.isEmpty(code)){
                return Resp.error("验证码错误");
            }

            if (!code.equals(verification)){
                return Resp.error("验证码错误");
            }
        }
        // 清除验证码
        CaptchaUtil.clear(request);
        // 获取请求的ip
        String ip = IpUtil.getIpAddress(request);
        try {
            User user1 = userService.selectAll(user);
            if (user1 != null) {
                // 登录成功
                CaptchaUtil.clear(request);
                // 清除错误次数
                ipInfo.getIpMap().remove(ip);
                if (user1.getLoginPermission() == 0){
                    return Resp.error("该用户已被禁用");
                }
                request.getSession().setAttribute(Constant.LOGIN_USER, user1);
                return Resp.ok("登录成功", "index");
            } else {
                ipInfo.getIpMap().put(ip, ipInfo.getIpMap().getOrDefault(ip, 0) + 1);
                return Resp.error("用户名或密码错误");
            }
        } catch (ServiceException e) {
            e.printStackTrace();
            return Resp.error("参数校验失败");
        }

    }

    @GetMapping("/index")
    public ModelAndView index() {
        return new ModelAndView("index");
    }
    @GetMapping("/q-login")
    public ModelAndView qqLogin() {
        return new ModelAndView("login/q-login");
    }
    @GetMapping("/phone-login")
    public ModelAndView phoneLogin() {
        return new ModelAndView("login/phone-login");
    }
    @PostMapping("/phone-login")
    public Resp phoneLogin(User user,String verification, HttpServletRequest request) {
        // 参数校验
        if (ObjectUtils.isEmpty(user) || !StringUtils.hasText(verification)) {
            return Resp.error("参数校验失败");
        }
        if (ObjectUtils.isEmpty(user.getPhone())) {
            return Resp.error("手机号不能为空");
        }
        if (redisTemplate.opsForValue().get(Constant.LOGIN_PHONE_CODE + user.getPhone()) == null){
            return Resp.error("验证码错误");
        }
        // 验证验证码是否过期
        if (redisTemplate.hasKey(Constant.LOGIN_PHONE_CODE + user.getPhone()) && redisTemplate.opsForValue().get(Constant.LOGIN_PHONE_CODE + user.getPhone()) == null){
            return Resp.error("验证码已过期");
        }
        // 验证验证码是否正确
        if (!redisTemplate.opsForValue().get(Constant.LOGIN_PHONE_CODE + user.getPhone()).equals(verification)){
            return Resp.error("验证码错误");
        }
        try {
            User user1 = userService.selectAll(user);
            if (user1 != null) {
                // 清除验证码
                redisTemplate.delete(Constant.LOGIN_PHONE_CODE + user.getPhone());
                // 清除错误次数
                ipInfo.getIpMap().remove(IpUtil.getIpAddress(request));
                if (user1.getLoginPermission() == 0){
                    return Resp.error("该用户已被禁用");
                }
                request.getSession().setAttribute(Constant.LOGIN_USER, user1);
                return Resp.ok("登录成功", "index");
            }
        }catch (ServiceException e){
            e.printStackTrace();
            return Resp.error("登录失败");
        }
        return Resp.ok();
    }
    @GetMapping("/sendPhoneCode")
    public Resp sendPhoneCode(User user) {
        // 验证手机号是否为空
        if (!StringUtils.hasText(user.getPhone())) {
            return Resp.error("手机号不能为空");
        }
        // 生成一个随机四位数
        String code = String.valueOf((int) (Math.random() * 9000 + 1000));
        // 将验证码存储到redis中,设置过期时间为5分钟
        redisTemplate.opsForValue().set(Constant.LOGIN_PHONE_CODE + user.getPhone(),code,5, TimeUnit.MINUTES);
        // 发送验证码
        try {
            SendSms.sendSms(user.getPhone(),code);
            return Resp.ok("发送成功");
        }catch (Exception e){
            e.printStackTrace();
            return Resp.error("发送失败");
        }
    }
    @GetMapping("/sendCode")
    public Resp sendCode(String email) {
        // 生成一个随机四位数
        String code = String.valueOf((int) (Math.random() * 9000 + 1000));
        // 将验证码存储到redis中,设置过期时间为5分钟
        redisTemplate.opsForValue().set(Constant.LOGIN_CODE + email,code,5, TimeUnit.MINUTES);
        if (StringUtils.hasText(email)) {
            try {
                emailService.sendCodeMail(email,code);
                return Resp.ok("发送成功");
            } catch (Exception e) {
                e.printStackTrace();
                return Resp.error("发送失败");
            }
        } else {
            return Resp.error("邮箱不能为空");
        }
    }
    @GetMapping("/forget")
    public ModelAndView forget() {
        return new ModelAndView("login/forget");
    }
    @GetMapping("/sendResetCode")
    public Resp reset(String email) {
        // 生成一个随机四位数
        String code = String.valueOf((int) (Math.random() * 9000 + 1000));

        // 将验证码存储到redis中,设置过期时间为5分钟
        redisTemplate.opsForValue().set(Constant.RESET_CODE + email,code,5, TimeUnit.MINUTES);

        if (StringUtils.hasText(email)) {
            try {
                emailService.sendResetMail(email,code);
                return Resp.ok("发送成功");
            } catch (Exception e) {
                e.printStackTrace();
                return Resp.error("发送失败");
            }
        } else {
            return Resp.error("邮箱不能为空");
        }
    }
    @PostMapping("/forget")
    public Resp forget(String email,User user,String verification) {
        // 验证数据有效性
        if (ObjectUtils.isEmpty(user) || !StringUtils.hasText(email) || !StringUtils.hasText(verification)){
            return Resp.error("参数校验失败");
        }
        // 从redis中获取验证码
        String code = (String) redisTemplate.opsForValue().get(Constant.RESET_CODE + email);
        if (ObjectUtils.isEmpty(code)){
            return Resp.error("验证码错误");
        }
        if (!code.equals(verification)){
            return Resp.error("验证码错误");
        }
        // 校验验证码是否过期
        if (redisTemplate.hasKey(Constant.RESET_CODE + email) && redisTemplate.opsForValue().get(Constant.RESET_CODE + email) == null){
            return Resp.error("验证码已过期");
        }
        // 校验邮箱格式
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")){
            return Resp.error("邮箱格式不正确");
        }
        try {
            Integer update = userService.update(user);
            if (update ==  1){
                // 从redis中删除验证码
                redisTemplate.delete(Constant.RESET_CODE + email);
                return Resp.ok("修改密码成功");
            }
            return Resp.error("邮箱未注册");
        } catch (ServiceException e) {
            return Resp.error(e.getMessage());
        }
    }
    @GetMapping("/publicKey")
    public Resp publicKey(){
        return Resp.ok(200,"success", RSAPairKey.getPublicKey());
    }
}
