package com.agro.trace.complaints.domain;

import com.agro.trace.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "complaints")
@Getter
@Setter
@NoArgsConstructor
public class Complaint extends BaseEntity {

    @Column(name = "complaint_id", unique = true, nullable = false, length = 64)
    private String complaintId;

    @Column(name = "complainant_uuid", nullable = false, length = 36)
    private String complainantUuid;

    @Column(name = "complainant_role", length = 32)
    private String complainantRole;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "description", nullable = false, length = 2000)
    private String description;

    @Lob
    @Column(name = "evidence_json", columnDefinition = "TEXT")
    private String evidenceJson;

    @Column(name = "related_lot_id", length = 64)
    private String relatedLotId;

    @Column(name = "related_organization_id")
    private Long relatedOrganizationId;

    @Column(name = "status", nullable = false, length = 32)
    private String status = "PENDING"; // PENDING, INVESTIGATING, RESOLVED, DISMISSED

    @Column(name = "assigned_officer_uuid", length = 36)
    private String assignedOfficerUuid;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "resolution", length = 2000)
    private String resolution;
}