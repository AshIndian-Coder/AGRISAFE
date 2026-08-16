package com.agro.trace.notifications.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MockNotificationProvider implements NotificationProvider {

    @Override
    public void sendSms(String mobileNumber, String message) {
        log.info("[MOCK SMS] To: {} | Message: {}", mobileNumber, message);
    }

    @Override
    public void sendEmail(String email, String subject, String body) {
        log.info("[MOCK EMAIL] To: {} | Subject: {} | Body length: {}", email, subject, body.length());
    }

    @Override
    public void sendPush(String deviceToken, String title, String body) {
        log.info("[MOCK PUSH] Device: {} | Title: {} | Body: {}", deviceToken, title, body);
    }
}
