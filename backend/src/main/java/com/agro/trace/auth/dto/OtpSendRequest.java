package com.agro.trace.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OtpSendRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email")
    String email
) {}
