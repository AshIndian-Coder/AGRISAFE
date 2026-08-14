package com.agrichain.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Standard API response wrapper
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private com.agrichain.common.dto.ErrorResponse error;
    private ResponseMeta meta;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .meta(ResponseMeta.builder()
                        .timestamp(Instant.now().toString())
                        .version("1.0")
                        .build())
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String traceId) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .meta(ResponseMeta.builder()
                        .traceId(traceId)
                        .timestamp(Instant.now().toString())
                        .version("1.0")
                        .build())
                .build();
    }

    public static ApiResponse<Void> error(com.agrichain.common.dto.ErrorResponse error) {
        return ApiResponse.<Void>builder()
                .success(false)
                .error(error)
                .meta(ResponseMeta.builder()
                        .timestamp(Instant.now().toString())
                        .build())
                .build();
    }

    public static ApiResponse<Void> error(com.agrichain.common.dto.ErrorResponse error, String traceId) {
        return ApiResponse.<Void>builder()
                .success(false)
                .error(error)
                .meta(ResponseMeta.builder()
                        .traceId(traceId)
                        .timestamp(Instant.now().toString())
                        .build())
                .build();
    }

    @Data
    @Builder
    public static class ResponseMeta {
        private String traceId;
        private String timestamp;
        private String version;
    }
}
