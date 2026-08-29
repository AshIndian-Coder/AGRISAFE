package com.agro.trace.qr.domain;

import com.agro.trace.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "qr_scan_history")
@Getter
@Setter
@NoArgsConstructor
public class QrScanHistory extends BaseEntity {

    @Column(name = "qr_id", nullable = false, length = 64)
    private String qrId;

    @Column(name = "rotating_code_hash", nullable = false, length = 128)
    private String rotatingCodeHash;

    @Column(name = "scan_timestamp", nullable = false)
    private Instant scanTimestamp;

    @Column(name = "scanned_by_uuid", length = 36)
    private String scannedByUuid;

    @Column(name = "scanned_by_role", length = 64)
    private String scannedByRole;

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "stage", length = 32)
    private String stage;

    @Column(name = "object_type", length = 32)
    private String objectType;

    @Column(name = "object_id", length = 64)
    private String objectId;

    @Column(name = "result", nullable = false, length = 20)
    private String result;
}
