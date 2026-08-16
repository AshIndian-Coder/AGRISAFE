package com.agro.trace.fraud.domain;

import com.agro.trace.common.domain.BaseEntity;
import com.agro.trace.common.domain.FlagSeverity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "flags")
@Getter
@Setter
@NoArgsConstructor
public class Flag extends BaseEntity {

    @Column(name = "flag_id", unique = true, nullable = false, length = 64)
    private String flagId;

    @Column(name = "flag_type", nullable = false, length = 64)
    private String flagType;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 20)
    private FlagSeverity severity = FlagSeverity.MEDIUM;

    @Column(name = "entity_type", length = 32)
    private String entityType; // LOT, PACKAGE, MANUFACTURER_LOT, BUNDLE, TEST, QR, USER

    @Column(name = "entity_id", length = 64)
    private String entityId;

    @Column(name = "actor_uuid", length = 36)
    private String actorUuid;

    @Column(name = "description", length = 2000)
    private String description;

    @Lob
    @Column(name = "evidence_json", columnDefinition = "TEXT")
    private String evidenceJson;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "OPEN"; // OPEN, INVESTIGATING, RESOLVED, DISMISSED

    @Column(name = "assigned_investigator_uuid", length = 36)
    private String assignedInvestigatorUuid;

    @Column(name = "resolution", length = 2000)
    private String resolution;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "resolved_by", length = 36)
    private String resolvedBy;

    @Column(name = "blockchain_ref", length = 128)
    private String blockchainRef;
}