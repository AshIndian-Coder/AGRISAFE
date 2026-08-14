package com.agrichain.identity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OtpRequestResponse {
    private boolean success;
    private Instant expiresAt;
    private String code; // Only in development
}
