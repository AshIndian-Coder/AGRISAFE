package com.agrichain.batch.entity;

import com.agrichain.common.enums.BatchStatus;

import java.util.*;

/**
 * State machine for batch lifecycle management
 */
public final class BatchStateMachine {

    private static final Map<BatchStatus, Set<BatchStatus>> ALLOWED_TRANSITIONS;

    static {
        Map<BatchStatus, Set<BatchStatus>> transitions = new EnumMap<>(BatchStatus.class);

        transitions.put(BatchStatus.CREATED, EnumSet.of(
            BatchStatus.PENDING_VERIFICATION,
            BatchStatus.RECALLED
        ));

        transitions.put(BatchStatus.PENDING_VERIFICATION, EnumSet.of(
            BatchStatus.VERIFIED,
            BatchStatus.REJECTED,
            BatchStatus.RECALLED
        ));

        transitions.put(BatchStatus.VERIFIED, EnumSet.of(
            BatchStatus.READY_FOR_MOVEMENT,
            BatchStatus.RECALLED
        ));

        transitions.put(BatchStatus.REJECTED, EnumSet.of(
            BatchStatus.CREATED,
            BatchStatus.DISPOSED,
            BatchStatus.DONATED
        ));

        transitions.put(BatchStatus.READY_FOR_MOVEMENT, EnumSet.of(
            BatchStatus.IN_TRANSIT,
            BatchStatus.RECALLED
        ));

        transitions.put(BatchStatus.IN_TRANSIT, EnumSet.of(
            BatchStatus.RECEIVED,
            BatchStatus.RECALLED
        ));

        transitions.put(BatchStatus.RECEIVED, EnumSet.of(
            BatchStatus.PROCESSING,
            BatchStatus.READY_FOR_MOVEMENT,
            BatchStatus.WHOLESALE,
            BatchStatus.RETAIL,
            BatchStatus.RECALLED
        ));

        transitions.put(BatchStatus.PROCESSING, EnumSet.of(
            BatchStatus.PROCESSED,
            BatchStatus.RECALLED
        ));

        transitions.put(BatchStatus.PROCESSED, EnumSet.of(
            BatchStatus.READY_FOR_MOVEMENT,
            BatchStatus.WHOLESALE,
            BatchStatus.RETAIL,
            BatchStatus.RECALLED
        ));

        transitions.put(BatchStatus.WHOLESALE, EnumSet.of(
            BatchStatus.READY_FOR_MOVEMENT,
            BatchStatus.RETAIL,
            BatchStatus.SOLD,
            BatchStatus.RECALLED,
            BatchStatus.EXPIRED
        ));

        transitions.put(BatchStatus.RETAIL, EnumSet.of(
            BatchStatus.SOLD,
            BatchStatus.RECALLED,
            BatchStatus.EXPIRED,
            BatchStatus.DONATED
        ));

        transitions.put(BatchStatus.SOLD, EnumSet.of(
            BatchStatus.RECALLED
        ));

        transitions.put(BatchStatus.RECALLED, EnumSet.of(
            BatchStatus.DISPOSED,
            BatchStatus.DONATED
        ));

        transitions.put(BatchStatus.EXPIRED, EnumSet.of(
            BatchStatus.DISPOSED,
            BatchStatus.DONATED
        ));

        transitions.put(BatchStatus.DONATED, EnumSet.noneOf(BatchStatus.class));
        transitions.put(BatchStatus.DISPOSED, EnumSet.noneOf(BatchStatus.class));

        ALLOWED_TRANSITIONS = Collections.unmodifiableMap(transitions);
    }

    private BatchStateMachine() {
        // Utility class
    }

    /**
     * Check if transition is allowed
     */
    public static boolean canTransition(BatchStatus current, BatchStatus target) {
        Set<BatchStatus> allowed = ALLOWED_TRANSITIONS.get(current);
        return allowed != null && allowed.contains(target);
    }

    /**
     * Get all allowed transitions from current state
     */
    public static Set<BatchStatus> getAllowedTransitions(BatchStatus current) {
        return ALLOWED_TRANSITIONS.getOrDefault(current, EnumSet.noneOf(BatchStatus.class));
    }

    /**
     * Check if state is terminal
     */
    public static boolean isTerminalState(BatchStatus status) {
        Set<BatchStatus> allowed = ALLOWED_TRANSITIONS.get(status);
        return allowed == null || allowed.isEmpty();
    }

    /**
     * Check if batch can be moved
     */
    public static boolean canBeMoved(BatchStatus status) {
        return status == BatchStatus.READY_FOR_MOVEMENT ||
               status == BatchStatus.PROCESSED ||
               status == BatchStatus.WHOLESALE;
    }

    /**
     * Check if batch requires verification
     */
    public static boolean requiresVerification(BatchStatus status) {
        return status == BatchStatus.PENDING_VERIFICATION;
    }
}
