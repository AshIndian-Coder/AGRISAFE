package com.agrichain.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for authorization failures
 */
public class AuthorizationException extends BaseException {

    public AuthorizationException(String code, String message, String messageKey) {
        super(code, message, messageKey, HttpStatus.FORBIDDEN);
    }

    public static AuthorizationException accessDenied() {
        return new AuthorizationException(
            "ACCESS_DENIED",
            "Access denied",
            "error.authorization.access_denied"
        );
    }

    public static AuthorizationException insufficientPermissions(String permission) {
        return new AuthorizationException(
            "INSUFFICIENT_PERMISSIONS",
            "Missing required permission: " + permission,
            "error.authorization.insufficient_permissions"
        );
    }

    public static AuthorizationException resourceOwnership(String resourceType) {
        return new AuthorizationException(
            "RESOURCE_OWNERSHIP_ERROR",
            "You do not have access to this " + resourceType,
            "error.authorization.resource_ownership"
        );
    }

    public static AuthorizationException organizationAccess() {
        return new AuthorizationException(
            "ORGANIZATION_ACCESS_ERROR",
            "You do not have access to this organization",
            "error.authorization.organization_access"
        );
    }
}
