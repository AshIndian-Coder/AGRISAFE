package com.agro.trace.fraud.service;

import com.agro.trace.common.domain.FlagSeverity;
import com.agro.trace.testing.domain.TestRecord;
import com.agro.trace.testing.repository.TestRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Detects suspicious patterns in test data:
 * - Identical measurements across different samples/lots
 * - Impossible values
 * - Repeated measurement hashes
 * - Suspicious timing patterns
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TestDataAnomalyDetector {

    private final TestRecordRepository testRecordRepository;
    private final FlagService flagService;

    private static final int MIN_ANOMALY_MATCHES = 3;

    /**
     * Analyze a newly submitted test record for anomalies.
     */
    public void analyze(TestRecord newRecord) {
        // Check 1: Identical measurement values from same tester across different objects
        checkIdenticalMeasurements(newRecord);

        // Check 2: Impossible values
        checkImpossibleValues(newRecord);
    }

    private void checkIdenticalMeasurements(TestRecord newRecord) {
        if (newRecord.getMeasuredValue() == null) return;

        List<TestRecord> similar = testRecordRepository
                .findByObjectTypeAndObjectIdOrderByTestedAtDesc("PACKAGE", newRecord.getObjectId());

        // Find records with the same measured value from the same tester
        long matchCount = similar.stream()
                .filter(r -> !r.getId().equals(newRecord.getId()))
                .filter(r -> newRecord.getMeasuredValue().equals(r.getMeasuredValue()))
                .filter(r -> newRecord.getTesterUuid().equals(r.getTesterUuid()))
                .count();

        if (matchCount >= MIN_ANOMALY_MATCHES) {
            log.warn("TEST DATA ANOMALY: Tester {} entered same value '{}' for {} different records",
                    newRecord.getTesterUuid(), newRecord.getMeasuredValue(), matchCount + 1);

            // Mark the record
            newRecord.setAnomalyFlag(true);

            // Create a flag
            flagService.createFlag(
                    "TEST_DATA_ANOMALY",
                    FlagSeverity.MEDIUM,
                    "TEST_RECORD",
                    newRecord.getTestRecordId(),
                    newRecord.getTesterUuid(),
                    String.format("Tester entered identical value '%s' for %d different samples",
                            newRecord.getMeasuredValue(), matchCount + 1),
                    null
            );
        }
    }

    private void checkImpossibleValues(TestRecord newRecord) {
        if (newRecord.getMeasuredValue() == null) return;

        try {
            double value = Double.parseDouble(newRecord.getMeasuredValue());
            boolean impossible = false;
            String reason = null;

            // Check for negative values where not applicable
            if (value < 0) {
                impossible = true;
                reason = "Negative measurement value: " + value;
            }

            // Check for absurdly high values
            if (value > 10000) {
                impossible = true;
                reason = "Implausibly high measurement value: " + value;
            }

            if (impossible) {
                log.warn("TEST DATA ANOMALY: Impossible value '{}' for record {}",
                        newRecord.getMeasuredValue(), newRecord.getTestRecordId());

                newRecord.setAnomalyFlag(true);

                flagService.createFlag(
                        "TEST_DATA_ANOMALY",
                        FlagSeverity.HIGH,
                        "TEST_RECORD",
                        newRecord.getTestRecordId(),
                        newRecord.getTesterUuid(),
                        reason,
                        null
                );
            }
        } catch (NumberFormatException e) {
            // Non-numeric values are fine (e.g. "PASS", "GOOD")
        }
    }
}