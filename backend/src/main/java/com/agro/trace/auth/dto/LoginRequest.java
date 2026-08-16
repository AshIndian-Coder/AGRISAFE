package com.agro.trace.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Identity (Aadhaar/PF/Employee ID/Mobile) is required")
        String identity,

        @NotBlank(message = "PIN is required")
        @Size(min = 6, max = 6, message = "PIN must be 6 digits")
        String pin
) {}