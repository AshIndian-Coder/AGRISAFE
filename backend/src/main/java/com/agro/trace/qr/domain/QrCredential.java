package com.agro.trace.qr.domain;

import com.agro.trace.common.domain.BaseEntity;
import com.agro.trace.common.domain.QrStatus;
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
@Table(name = "qr_credentials", indexes = {
    @jakarta.persistence.Index(name = "idx_qr_id", columnList = "qrId"),
    @jakarta.persistence.Index(name = "idx_qr_status", columnList = "status"),
    @jakarta.persistence.Index(name = "idx_qr_object", columnList = "objectType,objectId")
})
@Getter
@Setter
@NoArgsConstructor
public class QrCredential extends BaseEntity {

    @Column(name = "qr_id", unique = true, nullable = false, length = 64)
    private String qrId;

    @Column(name = "object_type", nullable = false, length = 32)
    private String objectType; // LOT, PACKAGE, MANUFACTURER_LOT, BUNDLE, PRODUCT_UNIT

    @Column(name = "object_id", nullable = false, length = 64)
    private String objectId;

    @Column(name = "stage", length = 32)
    private String stage;

    @Column(name = "dynamic_secret", length = 512)
    private String dynamicSecret;

    @Column(name = "secret_rotated_at")
    private Instant secretRotatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private QrStatus status = QrStatus.ACTIVE;

    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "consumed_at")
    private Instant consumedAt;

    @Column(name = "consumed_by", length = 36)
    private String consumedBy;

    @Column(name = "consumed_latitude", precision = 10, scale = 7)
    private BigDecimal consumedLatitude;

    @Column(name = "consumed_longitude", precision = 10, scale = 7)
    private BigDecimal consumedLongitude;

    @Column(name = "previous_qr_id", length = 64)
    private String previousQrId;

    @Column(name = "next_qr_id", length = 64)
    private String nextQrId;

    @Column(name = "blockchain_ref", length = 128)
    private String blockchainRef;
}