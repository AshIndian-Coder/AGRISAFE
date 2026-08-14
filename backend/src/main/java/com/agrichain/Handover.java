package com.agrichain.handover.entity;

import com.agrichain.batch.entity.Batch;
import com.agrichain.common.entity.BaseEntity;
import com.agrichain.common.enums.HandoverStatus;
import com.agrichain.identity.entity.User;
import com.agrichain.organization.entity.Organization;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Handover entity - represents custody transfer between organizations
 */
@Entity
@Table(name = "handovers", indexes = {
    @Index(name = "idx_handovers_batch", columnList = "batch_id"),
    @Index(name = "idx_handovers_from_org", columnList = "from_organization_id"),
    @Index(name = "idx_handovers_to_org", columnList = "to_organization_id"),
    @Index(name = "idx_handovers_status", columnList = "status"),
    @Index(name = "idx_handovers_scheduled", columnList = "scheduled_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Handover extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_organization_id", nullable = false)
    private Organization fromOrganization;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_user_id", nullable = false)
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_organization_id", nullable = false)
    private Organization toOrganization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id")
    private User toUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private HandoverStatus status = HandoverStatus.INITIATED;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 20)
    private HandoverStatus previousStatus;

    @Column(name = "quantity", nullable = false, precision = 15, scale = 4)
    private BigDecimal quantity;

    @Column(name = "accepted_quantity", precision = 15, scale = 4)
    private BigDecimal acceptedQuantity;

    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    @Column(name = "scheduled_date")
    private Instant scheduledDate;

    @Column(name = "initiated_at", nullable = false)
    @Builder.Default
    private Instant initiatedAt = Instant.now();

    @Column(name = "accepted_at")
    private Instant acceptedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "pickup_location", columnDefinition = "JSON")
    private String pickupLocation;

    @Column(name = "delivery_location", columnDefinition = "JSON")
    private String deliveryLocation;

    @Column(name = "actual_pickup_location", columnDefinition = "JSON")
    private String actualPickupLocation;

    @Column(name = "actual_delivery_location", columnDefinition = "JSON")
    private String actualDeliveryLocation;

    @Column(name = "transport_mode", length = 50)
    private String transportMode;

    @Column(name = "vehicle_number", length = 50)
    private String vehicleNumber;

    @Column(name = "driver_phone", length = 20)
    private String driverPhone;

    @Column(name = "expected_temperature_range", columnDefinition = "JSON")
    private String expectedTemperatureRange;

    @Column(name = "actual_condition", length = 50)
    private String actualCondition;

    @Column(name = "condition_notes", columnDefinition = "TEXT")
    private String conditionNotes;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "images", columnDefinition = "JSON")
    private String images;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "idempotency_key", unique = true, length = 128)
    private String idempotencyKey;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    public void transitionTo(HandoverStatus newStatus) {
        this.previousStatus = this.status;
        this.status = newStatus;
    }

    public void accept(BigDecimal acceptedQty, String condition) {
        this.acceptedQuantity = acceptedQty;
        this.actualCondition = condition;
        this.acceptedAt = Instant.now();
        transitionTo(HandoverStatus.ACCEPTED);
    }

    public void reject(String reason) {
        this.rejectionReason = reason;
        transitionTo(HandoverStatus.REJECTED);
    }

    public void complete() {
        this.completedAt = Instant.now();
        transitionTo(HandoverStatus.DELIVERED);
    }
}
