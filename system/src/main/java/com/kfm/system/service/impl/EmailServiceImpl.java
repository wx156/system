package com.kfm.system.service.impl;

import com.kfm.system.ex.ServiceException;
import com.kfm.system.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private MailProperties mailproperties;



    public EmailServiceImpl(JavaMailSender mailSender, MailProperties mailproperties) {
        this.mailSender = mailSender;
        this.mailproperties = mailproperties;
    }

    @Override
    public void sendEmail(String to, String subject, String text) throws ServiceException {
        // 验证数据有效性
        if (!(StringUtils.hasText(to) && StringUtils.hasText(subject) && StringUtils.hasText(text))) {
            throw new ServiceException("邮件发送失败，请检查邮箱地址、标题和内容是否正确");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailproperties.getUsername());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
    public void sendCodeMail(String to, String code) throws MailException, ServiceException {
        String text = "您的登录验证码是：" + code + "，请在5分钟内输入，切勿泄露！";
        sendEmail(to, "登录验证码", text);
    }
    @Override
    public void sendMail(String to, String subject, String body) throws MailException {
        String from = mailproperties.getUsername();
        // 发送html邮件
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(from);
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(body,true);
        };
        mailSender.send(messagePreparator);
    }

    public void sendActiveMail(String to, String activeUrl) throws MailException {
        String subject = "激活邮件";
        String body = "您的激活地址是: <a href='" + activeUrl + "'>激活地址</a>。链接 5 分钟内有效，请尽快激活！";
        sendMail(to, subject, body);
    }
    public void sendResetMail(String to, String code) throws MailException, ServiceException {
        String subject = "重置密码邮件";
        String body = "您的重置密码验证码是"+ code + "。验证码5分钟内有效，请尽快填写！";
        sendEmail(to, subject, body);
    }
}
