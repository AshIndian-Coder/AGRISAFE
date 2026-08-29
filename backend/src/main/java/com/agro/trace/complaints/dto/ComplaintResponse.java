package com.agro.trace.complaints.dto;

import java.time.Instant;

public record ComplaintResponse(
        String complaintId,
        String complainantUuid,
        String complainantRole,
        String category,
        String description,
        String relatedLotId,
        String status,
        String assignedOfficerUuid,
        String resolution,
        Instant createdAt,
        Instant resolvedAt
) {}