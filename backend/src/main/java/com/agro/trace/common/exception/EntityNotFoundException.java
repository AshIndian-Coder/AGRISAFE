package com.agro.trace.common.exception;

public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(String entityType, String identifier) {
        super("NOT_FOUND", entityType + " not found: " + identifier, 404);
    }

    public EntityNotFoundException(String message) {
        super("NOT_FOUND", message, 404);
    }
}