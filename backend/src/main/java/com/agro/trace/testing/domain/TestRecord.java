package com.agro.trace.testing.domain;

import com.agro.trace.common.domain.BaseEntity;
import com.agro.trace.common.domain.TestResult;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "test_records")
@Getter
@Setter
@NoArgsConstructor
public class TestRecord extends BaseEntity {

    @Column(name = "test_record_id", unique = true, nullable = false, length = 64)
    private String testRecordId;

    @Column(name = "object_type", nullable = false, length = 32)
    private String objectType; // LOT, PACKAGE, MANUFACTURER_LOT, BUNDLE

    @Column(name = "object_id", nullable = false, length = 64)
    private String objectId;

    @Column(name = "test_profile_id", nullable = false)
    private Long testProfileId;

    @Column(name = "test_definition_id")
    private Long testDefinitionId;

    @Column(name = "tester_uuid", nullable = false, length = 36)
    private String testerUuid;

    @Column(name = "measurement_source", length = 20)
    private String measurementSource = "SIMULATED"; // SIMULATED, DEVICE, MANUAL

    @Column(name = "device_id", length = 64)
    private String deviceId;

    @Column(name = "measured_value", length = 255)
    private String measuredValue;

    @Column(name = "unit", length = 50)
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false, length = 20)
    private TestResult result;

    @Column(name = "standard_version_id")
    private Long standardVersionId;

    @Column(name = "standard_name", length = 255)
    private String standardName;

    @Column(name = "min_threshold", length = 100)
    private String minThreshold;

    @Column(name = "max_threshold", length = 100)
    private String maxThreshold;

    @Column(name = "mandatory", nullable = false)
    private boolean mandatory = false;

    @Column(name = "tested_at", nullable = false)
    private Instant testedAt;

    @Column(name = "finalized", nullable = false)
    private boolean finalized = true;

    @Lob
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "anomaly_flag", nullable = false)
    private boolean anomalyFlag = false;

    @Column(name = "qr_id", length = 64)
    private String qrId;

    @Column(name = "blockchain_ref", length = 128)
    private String blockchainRef;
}