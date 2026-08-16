package com.agro.trace.manufacturing.domain;

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
@Table(name = "manufacturer_lots")
@Getter
@Setter
@NoArgsConstructor
public class ManufacturerLot extends BaseEntity {

    @Column(name = "manufacturer_lot_id", unique = true, nullable = false, length = 64)
    private String manufacturerLotId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "manufacturer_org_id")
    private Long manufacturerOrgId;

    @Column(name = "manufacturer_employee_uuid", length = 36)
    private String manufacturerEmployeeUuid;

    @Column(name = "production_quantity", precision = 15, scale = 3)
    private BigDecimal productionQuantity;

    @Column(name = "unit", length = 20)
    private String unit;

    @Column(name = "processing_date")
    private Instant processingDate;

    @Column(name = "facility_name", length = 255)
    private String facilityName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private LotStatus status = LotStatus.PROCESSED;

    @Column(name = "testing_status", length = 32)
    private String testingStatus = "NOT_TESTED";

    @Column(name = "qr_id", length = 64)
    private String qrId;

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
}