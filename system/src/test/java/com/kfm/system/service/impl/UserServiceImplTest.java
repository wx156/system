package com.kfm.system.service.impl;

import com.kfm.system.domain.User;
import com.kfm.system.ex.ServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserServiceImplTest {
    @Autowired
    private UserServiceImpl userService;
    @Test
    void selectAll() throws ServiceException {
        System.out.println(userService.selectAll(null));
    }

    @Test
    void insert() {
        try {
            User user = new User();
            user.setUsername("123");
            user.setPassword("123");

            userService.insert(user);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}