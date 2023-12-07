package com.kfm.system.controller;

import com.kfm.system.domain.User;
import com.kfm.system.ex.ServiceException;
import com.kfm.system.service.impl.EmailServiceImpl;
import com.kfm.system.service.impl.UserServiceImpl;
import com.kfm.system.util.Resp;
import com.wf.captcha.utils.CaptchaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
public class RegisterController {
    @Autowired
    UserServiceImpl userServiceImpl;
    @Autowired
    private EmailServiceImpl emailService;

    @GetMapping("/register")
    public ModelAndView register() {
        return new ModelAndView("register/register");
    }

    @GetMapping("/userRegister")
    public ModelAndView registerSuccess() {
        return new ModelAndView("register/userRegister");
    }

    @PostMapping("/userRegister")
    public Resp userRegister(User user, String captcha, HttpServletRequest request, String confirmPassword) {
        // 验证数据有效性
        if (ObjectUtils.isEmpty(user) || !StringUtils.hasText(captcha) || !StringUtils.hasText(confirmPassword)) {
            return Resp.error("参数校验失败");
        }
        if (!CaptchaUtil.ver(captcha, request)) {
            return Resp.error("验证码错误");
        }
        if (!confirmPassword.equals(user.getPassword())) {
            return Resp.error("两次密码不一致");
        }
        // 数据格式校验
        // 用户名格式为
        // 验证用户名是否重复
        try {
            User user1 = new User(user.getUsername());
            if (userServiceImpl.selectAll(user1) != null) {
                return Resp.error("用户名已存在");
            }
        } catch (ServiceException e) {
            return Resp.error(e.getMessage());
        }
        try {
            userServiceImpl.insert(user);
            return Resp.ok("注册成功", "login");
        } catch (ServiceException e) {
            return Resp.error(e.getMessage());
        }
    }

    @GetMapping("qRegister")
    public ModelAndView qRegister() {
        return new ModelAndView("register/qRegister");
    }

    @PostMapping("/qRegister")
    public Resp qRegister(User user, String confirmPassword) {
        if (ObjectUtils.isEmpty(user) || !StringUtils.hasText(confirmPassword)) {
            return Resp.error("参数校验失败");
        }
        if (!confirmPassword.equals(user.getPassword())) {
            return Resp.error("两次密码不一致");
        }
        try {
            User user1 = new User();
            user1.setEmail(user.getEmail());
            if (userServiceImpl.selectAll(user1) != null) {
                return Resp.error("邮箱已存在");
            }
            userServiceImpl.insert(user);
            return Resp.ok("注册成功", "login");
        } catch (ServiceException e) {
            return Resp.error(e.getMessage());
        }
    }

    @GetMapping("/qActivate")
    public Resp qActivate(String email, HttpServletRequest request) {
        // 验证数据有效性
        if (ObjectUtils.isEmpty(email)) {
            return Resp.error("参数校验失败");
        }

        // 发送邮件
        // 上下文路径
        String contextPath = request.getContextPath();
        // 获取当前的域名
        String serverName = request.getServerName();
        // 获取端口号
        int serverPort = request.getServerPort();
        // 协议
        String scheme = request.getScheme();
        // 地址
        String url = scheme + "://" + serverName + ":" + serverPort + contextPath + "/active?" + "email=" + email;
        // 发送邮件
        try {
            emailService.sendActiveMail(email, url);
            return Resp.ok("激活邮件已发送，请前往邮箱激活");
        } catch (Exception e) {
            e.printStackTrace();
            return Resp.error("激活邮件发送失败");
        }
    }

    @GetMapping("/active")
    public ModelAndView active(String email) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register/active");
        modelAndView.addObject("email", email);
        return modelAndView;
    }

    @GetMapping("/activate")
    public Resp activate(String email) {
        if (ObjectUtils.isEmpty(email)) {
            return Resp.error("参数校验失败");
        }

        User user = new User();
        user.setEmail(email);
        try {
            if (userServiceImpl.update(user) != 1){
                return Resp.error("激活失败");
            };
            return Resp.ok("激活成功", "login");
        }catch (ServiceException e){
            return Resp.error(e.getMessage());
        }
    }
}
