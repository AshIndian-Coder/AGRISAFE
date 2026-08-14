package com.agrichain.audit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * AuditLog entity - immutable audit trail for all operations
 */
@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_actor", columnList = "actor_id"),
    @Index(name = "idx_audit_resource", columnList = "resource_type, resource_id"),
    @Index(name = "idx_audit_action", columnList = "action"),
    @Index(name = "idx_audit_timestamp", columnList = "timestamp"),
    @Index(name = "idx_audit_trace", columnList = "trace_id")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "actor_id", updatable = false)
    private UUID actorId;

    @Column(name = "actor_organization_id", updatable = false)
    private UUID actorOrganizationId;

    @Column(name = "action", nullable = false, length = 50, updatable = false)
    private String action;

    @Column(name = "resource_type", nullable = false, length = 50, updatable = false)
    private String resourceType;

    @Column(name = "resource_id", updatable = false)
    private UUID resourceId;

    @Column(name = "previous_state", columnDefinition = "JSON", updatable = false)
    private String previousState;

    @Column(name = "new_state", columnDefinition = "JSON", updatable = false)
    private String newState;

    @Column(name = "changes", columnDefinition = "JSON", updatable = false)
    private String changes;

    @Column(name = "reason", columnDefinition = "TEXT", updatable = false)
    private String reason;

    @Column(name = "ip_address", length = 45, updatable = false)
    private String ipAddress;

    @Column(name = "user_agent", length = 500, updatable = false)
    private String userAgent;

    @Column(name = "trace_id", length = 128, updatable = false)
    private String traceId;

    @Column(name = "request_id", length = 128, updatable = false)
    private String requestId;

    @Column(name = "result", nullable = false, length = 20, updatable = false)
    private String result;

    @Column(name = "error_code", length = 50, updatable = false)
    private String errorCode;

    @Column(name = "metadata", columnDefinition = "JSON", updatable = false)
    private String metadata;

    @Column(name = "timestamp", nullable = false, updatable = false)
    @Builder.Default
    private Instant timestamp = Instant.now();
}
