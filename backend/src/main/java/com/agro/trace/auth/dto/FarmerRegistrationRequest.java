package com.agro.trace.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FarmerRegistrationRequest(
        @NotBlank(message = "Aadhaar reference is required")
        String aadhaarReference,

        @NotBlank(message = "OTP is required")
        @Size(min = 6, max = 6, message = "OTP must be 6 digits")
        String otp,

        @NotBlank(message = "PIN is required")
        @Size(min = 6, max = 6, message = "PIN must be 6 digits")
        String pin
) {}