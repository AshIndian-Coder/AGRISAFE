package com.agrichain.common.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Validation error detail
 */
@Data
@Builder
public class ValidationErrorResponse {
    private String field;
    private String code;
    private String message;
}
