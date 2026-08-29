package com.agro.trace.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Must be a valid email")
        String email,

        @NotBlank(message = "PIN is required")
        @Size(min = 6, max = 6, message = "PIN must be 6 digits")
        String pin
) {}