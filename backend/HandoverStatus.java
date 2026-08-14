package com.agrichain.common.enums;

/**
 * Handover/custody transfer states
 */
public enum HandoverStatus {
    INITIATED,
    PENDING_ACCEPTANCE,
    ACCEPTED,
    REJECTED,
    IN_TRANSIT,
    DELIVERED,
    CANCELLED,
    DISPUTED
}
