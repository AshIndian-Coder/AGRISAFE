package com.agrichain.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OtpVerifyDto {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+91[6-9]\\d{9}$", message = "Invalid Indian phone number format")
    private String phone;

    @NotBlank(message = "OTP is required")
    @Size(min = 6, max = 6, message = "OTP must be 6 digits")
    @Pattern(regexp = "^\\d{6}$", message = "OTP must contain only digits")
    private String otp;

    @Size(max = 128)
    private String deviceId;

    @Size(max = 128)
    private String deviceName;

    @Pattern(regexp = "^(IOS|ANDROID|WEB)$", message = "Invalid platform")
    private String devicePlatform;
}
