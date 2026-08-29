package com.agro.trace.notifications.service;

/**
 * Notification Provider Interface.
 * Supports SMS, email, and push notifications.
 * Mock implementation for prototype; connects to real providers in production.
 */
public interface NotificationProvider {

    void sendSms(String mobileNumber, String message);

    void sendEmail(String email, String subject, String body);

    void sendPush(String deviceToken, String title, String body);
}
