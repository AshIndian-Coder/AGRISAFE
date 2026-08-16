package com.agro.trace.idempotency;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "idempotency_keys")
@Getter
@Setter
public class IdempotencyRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 64)
    private String idempotencyKey;
    @Column(length = 10)
    private String requestMethod;
    @Column(length = 255)
    private String requestPath;
    private int responseStatus;
    @Column(columnDefinition = "TEXT")
    private String responseBody;
    @Column(nullable = false)
    private Instant createdAt = Instant.now();
    @Column(nullable = false)
    private Instant expiresAt;
}