package com.agro.trace.common.exception;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(String errorCode, String message) {
        super(errorCode, message, 401);
    }

    public UnauthorizedException(String message) {
        super("UNAUTHORIZED_ACCESS", message, 401);
    }
}