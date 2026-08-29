package com.agro.trace.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {
    private String pinHashType = "ARGON2ID";
    private int pinAttemptLimit = 5;
    private int pinLockoutDurationMinutes = 30;
    private int otpLength = 6;
    private int otpExpirationMinutes = 10;
    private int otpRateLimitPerMobile = 3;
    private int otpRateLimitWindowMinutes = 60;
}