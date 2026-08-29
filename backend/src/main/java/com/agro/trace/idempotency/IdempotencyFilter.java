package com.agro.trace.idempotency;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.agro.trace.common.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.time.Instant;

/**
 * Idempotency filter that prevents duplicate processing.
 * Client sends X-Idempotency-Key header on POST/PUT/PATCH/DELETE requests.
 * If the same key is reused, returns 409 DUPLICATE_REQUEST.
 */
@Component
@RequiredArgsConstructor
public class IdempotencyFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(IdempotencyFilter.class);

    private final IdempotencyKeyRepository repository;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String idempotencyKey = request.getHeader("X-Idempotency-Key");
        if (idempotencyKey == null || idempotencyKey.isBlank()
                || !isMutableMethod(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        var existing = repository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            log.info("Idempotent request blocked for key: {}", idempotencyKey);
            ApiResponse<Void> body = ApiResponse.error(409, "DUPLICATE_REQUEST",
                    "This request has already been processed. Key: " + idempotencyKey);
            response.setStatus(409);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(body));
            return;
        }

        filterChain.doFilter(request, response);

        if (response.getStatus() >= 200 && response.getStatus() < 500) {
            saveKey(idempotencyKey, request.getMethod(), request.getRequestURI(), response.getStatus());
        }
    }

    protected void saveKey(String key, String method, String path, int status) {
        IdempotencyRecord record = new IdempotencyRecord();
        record.setIdempotencyKey(key);
        record.setRequestMethod(method);
        record.setRequestPath(path);
        record.setResponseStatus(status);
        record.setExpiresAt(Instant.now().plusSeconds(86400));
        repository.save(record);
    }

    private boolean isMutableMethod(String method) {
        return "POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)
                || "PATCH".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method);
    }
}