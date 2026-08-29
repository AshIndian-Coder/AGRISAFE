package com.agro.trace.common.exception;

public class ForbiddenException extends BusinessException {

    public ForbiddenException(String errorCode, String message) {
        super(errorCode, message, 403);
    }

    public ForbiddenException(String message) {
        super("FORBIDDEN_ACCESS", message, 403);
    }
}