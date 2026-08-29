package com.agro.trace.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OtpVerifyRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email")
    String email,

    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "\\d{6}", message = "OTP must be 6 digits")
    String otp
) {}
