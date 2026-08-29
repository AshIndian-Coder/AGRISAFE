package com.agro.trace.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EmployeeRegistrationRequest(
        @NotBlank(message = "Employee ID is required")
        String employeeId,

        @NotBlank(message = "Aadhaar reference is required")
        String aadhaarReference,

        @NotNull(message = "Organization ID is required")
        Long organizationId,

        @NotBlank(message = "OTP is required")
        @Size(min = 6, max = 6, message = "OTP must be 6 digits")
        String otp,

        @NotBlank(message = "PIN is required")
        @Size(min = 6, max = 6, message = "PIN must be 6 digits")
        String pin,

        @NotBlank(message = "User type is required")
        String userType,

        String functionalType
) {}