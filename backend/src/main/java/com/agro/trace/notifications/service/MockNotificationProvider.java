package com.agro.trace.notifications.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@Slf4j
public class MockNotificationProvider implements NotificationProvider {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from-address:noreply@agrisafe.in}")
    private String fromAddress;

    public MockNotificationProvider(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendSms(String mobileNumber, String message) {
        log.info("[SMS] To: {} | Message: {}", mobileNumber, message);
    }

    @Override
    public void sendEmail(String email, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body, false);
            mailSender.send(message);
            log.info("Email sent to {} | Subject: {}", email, subject);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", email, e.getMessage());
        }
    }

    @Override
    public void sendPush(String deviceToken, String title, String body) {
        log.info("[PUSH] Device: {} | Title: {} | Body: {}", deviceToken, title, body);
    }
}
