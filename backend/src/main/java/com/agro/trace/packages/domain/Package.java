package com.agro.trace.packages.domain;

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

@Entity
@Table(name = "packages")
@Getter
@Setter
@NoArgsConstructor
public class Package extends BaseEntity {

    @Column(name = "package_id", unique = true, nullable = false, length = 64)
    private String packageId;

    @Column(name = "lot_id", nullable = false, length = 64)
    private String lotId;

    @Column(name = "quantity", nullable = false, precision = 15, scale = 3)
    private BigDecimal quantity;

    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    @Column(name = "package_type", length = 50)
    private String packageType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private LotStatus status = LotStatus.AT_NODAL_CENTER;

    @Column(name = "current_custodian_uuid", length = 36)
    private String currentCustodianUuid;

    @Column(name = "current_custodian_role", length = 32)
    private String currentCustodianRole;

    @Column(name = "qr_id", length = 64)
    private String qrId;

    @Column(name = "parent_package_id", length = 64)
    private String parentPackageId;

    @Column(name = "testing_status", length = 32)
    private String testingStatus = "NOT_TESTED";

    @Column(name = "blockchain_ref", length = 128)
    private String blockchainRef;

    @Column(name = "data_hash", length = 128)
    private String dataHash;

    @Column(name = "recalled", nullable = false)
    private boolean recalled = false;

    @Column(name = "quarantined", nullable = false)
    private boolean quarantined = false;

    @Column(name = "notes", length = 2000)
    private String notes;
}