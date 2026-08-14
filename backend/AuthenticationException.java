package com.agrichain.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for authentication failures
 */
public class AuthenticationException extends BaseException {

    public AuthenticationException(String code, String message, String messageKey) {
        super(code, message, messageKey, HttpStatus.UNAUTHORIZED);
    }

    public static AuthenticationException invalidCredentials() {
        return new AuthenticationException(
            "INVALID_CREDENTIALS",
            "Invalid credentials provided",
            "error.authentication.invalid_credentials"
        );
    }

    public static AuthenticationException tokenExpired() {
        return new AuthenticationException(
            "TOKEN_EXPIRED",
            "Authentication token has expired",
            "error.authentication.token_expired"
        );
    }

    public static AuthenticationException invalidToken() {
        return new AuthenticationException(
            "INVALID_TOKEN",
            "Invalid authentication token",
            "error.authentication.invalid_token"
        );
    }

    public static AuthenticationException sessionRevoked() {
        return new AuthenticationException(
            "SESSION_REVOKED",
            "Session has been revoked",
            "error.authentication.session_revoked"
        );
    }
}
