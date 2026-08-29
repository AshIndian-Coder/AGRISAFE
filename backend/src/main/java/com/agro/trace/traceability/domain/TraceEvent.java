package com.agro.trace.traceability.domain;

import com.agro.trace.common.domain.ActionType;
import com.agro.trace.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "trace_events", indexes = {
    @Index(name = "idx_trace_object", columnList = "objectType,objectId"),
    @Index(name = "idx_trace_actor", columnList = "actorUuid"),
    @Index(name = "idx_trace_timestamp", columnList = "eventTimestamp"),
    @Index(name = "idx_trace_action", columnList = "action")
})
@Getter
@Setter
@NoArgsConstructor
public class TraceEvent extends BaseEntity {

    @Column(name = "event_id", unique = true, nullable = false, length = 64)
    private String eventId;

    @Column(name = "object_type", nullable = false, length = 32)
    private String objectType; // LOT, PACKAGE, MANUFACTURER_LOT, BUNDLE, PRODUCT_UNIT

    @Column(name = "object_id", nullable = false, length = 64)
    private String objectId;

    @Column(name = "actor_uuid", nullable = false, length = 36)
    private String actorUuid;

    @Column(name = "actor_role", length = 32)
    private String actorRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 50)
    private ActionType action;

    @Column(name = "previous_state", length = 32)
    private String previousState;

    @Column(name = "new_state", length = 32)
    private String newState;

    @Column(name = "event_timestamp", nullable = false)
    private Instant eventTimestamp;

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "gps_accuracy")
    private Double gpsAccuracy;

    @Column(name = "qr_id", length = 64)
    private String qrId;

    @Column(name = "device_info", length = 255)
    private String deviceInfo;

    @Column(name = "test_reference", length = 64)
    private String testReference;

    @Column(name = "blockchain_ref", length = 128)
    private String blockchainRef;

    @Lob
    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;

    @Column(name = "event_hash", length = 128)
    private String eventHash;

    @Column(name = "trace_id", length = 64)
    private String traceId;
}