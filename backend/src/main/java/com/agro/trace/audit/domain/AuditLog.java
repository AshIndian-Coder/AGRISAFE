package com.agro.trace.audit.domain;

import com.agro.trace.common.domain.ActionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_actor", columnList = "actorUuid"),
    @Index(name = "idx_audit_action", columnList = "action"),
    @Index(name = "idx_audit_timestamp", columnList = "timestamp"),
    @Index(name = "idx_audit_entity", columnList = "entityType,entityId")
})
@Getter
@Setter
@NoArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 36)
    private String actorUuid;

    @Column(length = 32)
    private String actorRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ActionType action;

    @Column(length = 32)
    private String entityType;

    @Column(length = 64)
    private String entityId;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(length = 64)
    private String ipAddress;

    @Column(length = 64)
    private String traceId;

    @Column(length = 64)
    private String correlationId;

    @Column(length = 32)
    private String dataClassification; // PUBLIC, OPERATIONAL, SENSITIVE, GOVERNMENT_ONLY
}