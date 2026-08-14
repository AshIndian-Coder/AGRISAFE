package com.agrichain.identity.dto;

import com.agrichain.common.enums.UserRole;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserDto {
    private UUID id;
    private String phone;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;
    private UUID organizationId;
    private String preferredLanguage;
    private String profileImageUrl;
    private Boolean isPhoneVerified;
    private Boolean isEmailVerified;
}
