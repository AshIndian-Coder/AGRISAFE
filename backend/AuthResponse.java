package com.agrichain.identity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private UserDto user;
    private TokensDto tokens;
    private boolean isNewUser;
}
