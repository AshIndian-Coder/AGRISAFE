package com.agro.trace.common.exception;

public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(String errorCode, String message) {
        super(errorCode, message, 409);
    }
}