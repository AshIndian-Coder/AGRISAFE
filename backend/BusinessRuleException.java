package com.agrichain.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for business rule violations
 */
public class BusinessRuleException extends BaseException {

    public BusinessRuleException(String code, String message, String messageKey) {
        super(code, message, messageKey, HttpStatus.BAD_REQUEST);
    }

    public static BusinessRuleException invalidStateTransition(String entityType, String currentState, String targetState) {
        return new BusinessRuleException(
            "INVALID_STATE_TRANSITION",
            String.format("Cannot transition %s from %s to %s", entityType, currentState, targetState),
            "error.business.invalid_state_transition"
        );
    }

    public static BusinessRuleException duplicateResource(String resourceType, String identifier) {
        return new BusinessRuleException(
            "DUPLICATE_RESOURCE",
            String.format("%s with identifier %s already exists", resourceType, identifier),
            "error.business.duplicate_resource"
        );
    }

    public static BusinessRuleException concurrencyConflict(String resourceType) {
        return new BusinessRuleException(
            "CONCURRENCY_CONFLICT",
            String.format("%s was modified by another request. Please refresh and try again.", resourceType),
            "error.concurrency.conflict"
        );
    }
}
