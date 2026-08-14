package com.agrichain.batch.entity;

import com.agrichain.common.entity.BaseEntity;
import com.agrichain.common.enums.BatchStatus;
import com.agrichain.farm.entity.Farm;
import com.agrichain.farmer.entity.Farmer;
import com.agrichain.organization.entity.Organization;
import com.agrichain.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Batch entity - represents a batch of agricultural produce
 */
@Entity
@Table(name = "batches", indexes = {
    @Index(name = "idx_batches_product", columnList = "product_id"),
    @Index(name = "idx_batches_farm", columnList = "farm_id"),
    @Index(name = "idx_batches_farmer", columnList = "farmer_id"),
    @Index(name = "idx_batches_organization", columnList = "organization_id"),
    @Index(name = "idx_batches_current_holder", columnList = "current_holder_id"),
    @Index(name = "idx_batches_status", columnList = "status"),
    @Index(name = "idx_batches_harvest_date", columnList = "harvest_date"),
    @Index(name = "idx_batches_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Batch extends BaseEntity {

    @Column(name = "batch_code", nullable = false, unique = true, length = 50)
    private String batchCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_holder_id")
    private Organization currentHolder;

    @Column(name = "quantity", nullable = false, precision = 15, scale = 4)
    private BigDecimal quantity;

    @Column(name = "remaining_quantity", nullable = false, precision = 15, scale = 4)
    private BigDecimal remainingQuantity;

    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private BatchStatus status = BatchStatus.CREATED;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 30)
    private BatchStatus previousStatus;

    @Column(name = "harvest_date", nullable = false)
    private Instant harvestDate;

    @Column(name = "expected_expiry_date")
    private Instant expectedExpiryDate;

    @Column(name = "farming_method", length = 50)
    private String farmingMethod;

    @Column(name = "cultivation_start_date")
    private Instant cultivationStartDate;

    @Column(name = "location", columnDefinition = "JSON")
    private String location;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "certifications", columnDefinition = "JSON")
    private String certifications;

    @Column(name = "quality_grade", length = 50)
    private String qualityGrade;

    @Column(name = "price_per_unit", precision = 15, scale = 4)
    private BigDecimal pricePerUnit;

    @Column(name = "currency", length = 3)
    @Builder.Default
    private String currency = "INR";

    @Column(name = "qr_code_url", length = 2048)
    private String qrCodeUrl;

    @Column(name = "risk_score", precision = 5, scale = 2)
    private BigDecimal riskScore;

    @Column(name = "risk_score_updated_at")
    private Instant riskScoreUpdatedAt;

    @Column(name = "client_operation_id", unique = true, length = 128)
    private String clientOperationId;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    public void transitionTo(BatchStatus newStatus) {
        this.previousStatus = this.status;
        this.status = newStatus;
    }

    public boolean canTransitionTo(BatchStatus targetStatus) {
        return BatchStateMachine.canTransition(this.status, targetStatus);
    }
}
