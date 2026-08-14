package com.agrichain.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base exception for all domain exceptions
 */
@Getter
public abstract class BaseException extends RuntimeException {

    private final String code;
    private final String messageKey;
    private final HttpStatus httpStatus;
    private final boolean operational;

    protected BaseException(String code, String message, String messageKey, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.messageKey = messageKey;
        this.httpStatus = httpStatus;
        this.operational = true;
    }

    protected BaseException(String code, String message, String messageKey, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.messageKey = messageKey;
        this.httpStatus = httpStatus;
        this.operational = true;
    }
}
