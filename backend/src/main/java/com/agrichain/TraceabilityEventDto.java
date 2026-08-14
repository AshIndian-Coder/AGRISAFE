package com.agrichain.batch.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class TraceabilityEventDto {
    private UUID id;
    private String eventType;
    private String previousState;
    private String newState;
    private String actorName;
    private String actorOrganization;
    private Instant timestamp;
}
