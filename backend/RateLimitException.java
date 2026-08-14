package com.agrichain.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Exception for rate limit exceeded
 */
@Getter
public class RateLimitException extends BaseException {

    private final int retryAfterSeconds;

    public RateLimitException(int retryAfterSeconds) {
        super(
            "RATE_LIMIT_EXCEEDED",
            "Too many requests. Please try again later.",
            "error.rate_limit.exceeded",
            HttpStatus.TOO_MANY_REQUESTS
        );
        this.retryAfterSeconds = retryAfterSeconds;
    }
}
