package com.dying.service.impl;

import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

/**
 * @Author daylight
 */
@Service
public class emailServiceImpl {
    @Resource
    private JavaMailSender mailSender;

    public String sendEmailBackCode(String to) throws MessagingException, UnsupportedEncodingException {
        String code = RandomUtil.randomNumbers(6);
        String text=code+ "";

        MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("trueseestars@qq.com","验证码提醒");
            helper.setSubject("Hello");
            helper.setTo(to);
            helper.setText(text, true);
            mailSender.send(message);
        return code;
    }
}