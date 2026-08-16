package com.agro.trace.common.exception;

public class InvalidStateTransitionException extends BusinessException {

    public InvalidStateTransitionException(String message) {
        super("INVALID_STATE_TRANSITION", message, 400);
    }

    public InvalidStateTransitionException(String errorCode, String message) {
        super(errorCode, message, 400);
    }
}