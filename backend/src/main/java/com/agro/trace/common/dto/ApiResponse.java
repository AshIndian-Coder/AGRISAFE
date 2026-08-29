package com.agro.trace.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        int status,
        String code,
        String message,
        T data,
        String traceId,
        Instant timestamp
) {
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Success");
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(200)
                .code("SUCCESS")
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(201)
                .code("CREATED")
                .message("Resource created successfully")
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    public static ApiResponse<Void> error(int status, String code, String message) {
        return ApiResponse.<Void>builder()
                .success(false)
                .status(status)
                .code(code)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    public static ApiResponse<Void> error(int status, String code, String message, String traceId) {
        return ApiResponse.<Void>builder()
                .success(false)
                .status(status)
                .code(code)
                .message(message)
                .traceId(traceId)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> of(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(200)
                .code("SUCCESS")
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }
}