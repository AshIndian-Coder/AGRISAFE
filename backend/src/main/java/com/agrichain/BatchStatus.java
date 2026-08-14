package com.agrichain.common.enums;

/**
 * Batch lifecycle states
 */
public enum BatchStatus {
    CREATED,
    PENDING_VERIFICATION,
    VERIFIED,
    REJECTED,
    READY_FOR_MOVEMENT,
    IN_TRANSIT,
    RECEIVED,
    PROCESSING,
    PROCESSED,
    WHOLESALE,
    RETAIL,
    SOLD,
    RECALLED,
    EXPIRED,
    DONATED,
    DISPOSED
}
