package com.agrichain.identity.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class SessionDto {
    private UUID id;
    private String deviceId;
    private String deviceName;
    private String devicePlatform;
    private Instant lastUsedAt;
    private Instant createdAt;
    private boolean isCurrent;
}
