package com.agro.trace.bundles.domain;

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
@Table(name = "bundles")
@Getter
@Setter
@NoArgsConstructor
public class Bundle extends BaseEntity {

    @Column(name = "bundle_id", unique = true, nullable = false, length = 64)
    private String bundleId;

    @Column(name = "manufacturer_lot_id", length = 64)
    private String manufacturerLotId;

    @Column(name = "bundle_type", length = 50)
    private String bundleType; // CARTON, CASE, BOX, TRAY, PACK

    @Column(name = "quantity", precision = 15, scale = 3)
    private BigDecimal quantity;

    @Column(name = "unit", length = 20)
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private LotStatus status = LotStatus.BUNDLED;

    @Column(name = "current_custodian_uuid", length = 36)
    private String currentCustodianUuid;

    @Column(name = "current_custodian_role", length = 32)
    private String currentCustodianRole;

    @Column(name = "qr_id", length = 64)
    private String qrId;

    @Column(name = "blockchain_ref", length = 128)
    private String blockchainRef;

    @Column(name = "data_hash", length = 128)
    private String dataHash;

    @Column(name = "recalled", nullable = false)
    private boolean recalled = false;

    @Column(name = "quarantined", nullable = false)
    private boolean quarantined = false;

    @Column(name = "retailer_received", nullable = false)
    private boolean retailerReceived = false;

    @Column(name = "retailer_received_at")
    private Instant retailerReceivedAt;

    @Column(name = "retailer_uuid", length = 36)
    private String retailerUuid;

    @Column(name = "distributor_verified", nullable = false)
    private boolean distributorVerified = false;

    @Column(name = "notes", length = 2000)
    private String notes;
}