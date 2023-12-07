package com.kfm.system.service.impl;




import com.kfm.system.domain.User;
import com.kfm.system.ex.ServiceException;
import com.kfm.system.mapper.UserMapper;
import com.kfm.system.service.UserService;
import com.kfm.system.util.MD5Util;
import com.kfm.system.util.RSAPairKey;
import com.kfm.system.util.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
* @author wangxu
* @description 针对表【user】的数据库操作Service实现
* @createDate 2023-11-10 20:29:11
*/
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public User selectAll(User user) throws ServiceException {
        // 参数校验
        if (ObjectUtils.isEmpty(user)) {
            throw new ServiceException("参数不能为空");
        }
        String pass = "";
        // 密码解密
        if (!ObjectUtils.isEmpty(user.getPassword())) {
            // 1.解密password
            pass = RSAUtil.decryptWithPrivateKeyBlock(user.getPassword(), RSAPairKey.getPrivateKey());
        }
        if (!ObjectUtils.isEmpty(user.getUsername()) && !ObjectUtils.isEmpty(user.getPassword())) {
            // 密码加密
            user.setPassword(MD5Util.encodePassword(pass, user.getUsername()));
        }
        if (!ObjectUtils.isEmpty(user.getEmail()) && !ObjectUtils.isEmpty(user.getPassword())) {
            user.setPassword(MD5Util.encodePassword(pass, user.getEmail()));
        }
        if (!ObjectUtils.isEmpty(user.getPhone()) &&!ObjectUtils.isEmpty(user.getPassword())) {
            user.setPassword(MD5Util.encodePassword(pass, user.getPhone()));
        }
        List<User> users = userMapper.selectAll(user);
        if (users.size() == 1){
            User user1 = users.get(0);
            return user1;
        }else {
            return null;
        }
    }

    @Override
    public Integer insert(User user) throws ServiceException {
        // 校验参数
        if (ObjectUtils.isEmpty(user)) {
            throw new ServiceException("参数不能为空");
        }
        // 密码加密

        if (user.getUsername() != null && user.getPassword()!= null){
            user.setPassword(MD5Util.encodePassword(user.getPassword(), user.getUsername()));
            user.setLoginPermission(1);
        }
        if (user.getEmail() != null && user.getPassword() != null){
            user.setPassword(MD5Util.encodePassword(user.getPassword(), user.getEmail()));
        }
        if (user.getPhone()!= null && user.getPassword()!= null){
            user.setPassword(MD5Util.encodePassword(user.getPassword(), user.getPhone()));
        }
        Integer integer = userMapper.insertSelective(user);
        if (integer == 1) {
            return 1;
        } else {
            throw new ServiceException("注册失败");
        }
    }

    @Override
    public Integer update(User user) throws ServiceException {
        if (user == null) {
            throw new ServiceException("参数不能为空");
        }
        if (user.getEmail()!=null && user.getPassword()!=null){
            user.setPassword(MD5Util.encodePassword(user.getPassword(), user.getEmail()));
            Integer integer = userMapper.updatePasswordByUser(user);
            if (integer == 1) {
                return 1;
            }
            return null;
        }
        Integer update = userMapper.updateByUser(user);
        if (update == 1) {
            return 1;
        }
        return null;
    }
}




