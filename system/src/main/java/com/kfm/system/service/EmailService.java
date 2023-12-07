package com.kfm.system.service;

import com.kfm.system.ex.ServiceException;
import org.springframework.mail.MailException;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;

public interface EmailService {
    void sendEmail(String to, String subject, String text) throws ServiceException;
    void sendMail(String to, String subject, String body) throws MailException;
}
