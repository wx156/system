package com.kfm.system.service.impl;

import com.kfm.system.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class EmailServiceImplTest {
    @Autowired
    private EmailServiceImpl emailService;

}