package com.agro.trace.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AuthResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") long expiresIn,
        @JsonProperty("user_uuid") String userUuid,
        @JsonProperty("user_name") String userName,
        @JsonProperty("user_type") String userType,
        String role,
        @JsonProperty("organization_id") Long organizationId
) {}