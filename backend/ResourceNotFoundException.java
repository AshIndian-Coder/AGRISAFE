package com.agrichain.common.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

/**
 * Exception thrown when a requested resource is not found
 */
public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(String resourceType, UUID resourceId) {
        super(
            resourceType.toUpperCase() + "_NOT_FOUND",
            String.format("%s with ID %s not found", resourceType, resourceId),
            "error.not_found." + resourceType.toLowerCase(),
            HttpStatus.NOT_FOUND
        );
    }

    public ResourceNotFoundException(String resourceType, String identifier) {
        super(
            resourceType.toUpperCase() + "_NOT_FOUND",
            String.format("%s not found: %s", resourceType, identifier),
            "error.not_found." + resourceType.toLowerCase(),
            HttpStatus.NOT_FOUND
        );
    }
}
