package com.agro.trace.lots.domain;

import com.agro.trace.common.domain.BaseEntity;
import com.agro.trace.common.domain.LotStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "lots")
@Getter
@Setter
@NoArgsConstructor
public class Lot extends BaseEntity {

    @Column(name = "lot_id", unique = true, nullable = false, length = 64)
    private String lotId;

    @Column(name = "farmer_uuid", nullable = false, length = 36)
    private String farmerUuid;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "variety_id")
    private Long varietyId;

    @Column(name = "quantity", nullable = false, precision = 15, scale = 3)
    private BigDecimal quantity;

    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private LotStatus status = LotStatus.CREATED;

    @Column(name = "origin_latitude", precision = 10, scale = 7)
    private BigDecimal originLatitude;

    @Column(name = "origin_longitude", precision = 10, scale = 7)
    private BigDecimal originLongitude;

    @Column(name = "origin_address", length = 500)
    private String originAddress;

    @Column(name = "estimated_value", precision = 15, scale = 2)
    private BigDecimal estimatedValue;

    @Column(name = "pricing_reference", length = 64)
    private String pricingReference;

    @Column(name = "current_custodian_uuid", length = 36)
    private String currentCustodianUuid;

    @Column(name = "current_custodian_role", length = 32)
    private String currentCustodianRole;

    @Column(name = "qr_id", length = 64)
    private String qrId;

    @Column(name = "accepted_at")
    private Instant acceptedAt;

    @Column(name = "accepted_by", length = 36)
    private String acceptedBy;

    @Column(name = "nodal_center_id")
    private Long nodalCenterId;

    @Column(name = "blockchain_ref", length = 128)
    private String blockchainRef;

    @Column(name = "data_hash", length = 128)
    private String dataHash;

    @Column(name = "recalled", nullable = false)
    private boolean recalled = false;

    @Column(name = "recalled_at")
    private Instant recalledAt;

    @Column(name = "recall_reason", length = 1000)
    private String recallReason;

    @Column(name = "notes", length = 2000)
    private String notes;
}