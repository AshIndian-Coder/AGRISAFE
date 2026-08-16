package com.agro.trace.testing.service;

import com.agro.trace.common.domain.ActionType;
import com.agro.trace.common.domain.FlagSeverity;
import com.agro.trace.common.domain.TestResult;
import com.agro.trace.common.exception.BusinessException;
import com.agro.trace.common.exception.EntityNotFoundException;
import com.agro.trace.packages.domain.Package;
import com.agro.trace.packages.repository.PackageRepository;
import com.agro.trace.standards.domain.StandardRequirement;
import com.agro.trace.standards.repository.StandardRequirementRepository;
import com.agro.trace.testing.domain.TestRecord;
import com.agro.trace.testing.dto.TestSubmitRequest;
import com.agro.trace.testing.dto.TestResultResponse;
import com.agro.trace.testing.repository.TestRecordRepository;
import com.agro.trace.traceability.service.TraceEventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestingService {

    private final TestRecordRepository testRecordRepository;
    private final PackageRepository packageRepository;
    private final StandardRequirementRepository standardRequirementRepository;
    private final TraceEventService traceEventService;
    private final com.agro.trace.qr.service.QrService qrService;
    private final com.agro.trace.fraud.service.FlagService flagService;
    private final com.agro.trace.fraud.service.TestDataAnomalyDetector anomalyDetector;

    @Transactional
    public TestResultResponse submitTest(TestSubmitRequest request, String testerUuid) {
        // Verify the package exists
        Package pkg = packageRepository.findByPackageId(request.packageId())
                .orElseThrow(() -> new EntityNotFoundException("Package", request.packageId()));

        // Consume QR if provided (tester scans the package QR before testing)
        if (request.qrId() != null) {
            qrService.consumeQr(request.qrId(), testerUuid, null, null);
        }

        // Get the applicable standard requirement
        StandardRequirement standard = null;
        if (request.standardRequirementId() != null) {
            standard = standardRequirementRepository.findById(request.standardRequirementId())
                    .orElse(null);
        }

        // Evaluate the test result against standard
        TestResult result = evaluateTest(request.measuredValue(), standard);

        String testRecordId = "TEST-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();

        TestRecord record = new TestRecord();
        record.setTestRecordId(testRecordId);
        record.setObjectType("PACKAGE");
        record.setObjectId(request.packageId());
        record.setTestProfileId(request.testProfileId());
        record.setTestDefinitionId(request.testDefinitionId());
        record.setTesterUuid(testerUuid);
        record.setMeasurementSource("SIMULATED");
        record.setMeasuredValue(request.measuredValue());
        record.setUnit(request.unit());
        record.setResult(result);
        if (standard != null) {
            record.setStandardVersionId(standard.getStandardId());
            record.setStandardName(standard.getTestName());
            record.setMinThreshold(standard.getMinimumValue() != null ? standard.getMinimumValue().toString() : null);
            record.setMaxThreshold(standard.getMaximumValue() != null ? standard.getMaximumValue().toString() : null);
        }
        record.setMandatory(request.mandatory());
        record.setTestedAt(Instant.now());
        record.setFinalized(true);
        record.setQrId(request.qrId());

        record = testRecordRepository.save(record);

        // Update package testing status
        updatePackageTestingStatus(pkg, result);

        // If test FAILED, create a flag for government investigation
        if (result == TestResult.FAIL && request.mandatory()) {
            flagService.createFlag("QUALITY_TEST_FAILED", FlagSeverity.HIGH,
                    "PACKAGE", request.packageId(),
                    testerUuid,
                    "Mandatory test FAILED: " + (standard != null ? standard.getTestName() : "Unknown test")
                    + " (value: " + request.measuredValue() + " "
                    + request.unit() + ", required: "
                    + (standard != null ? standard.getMinimumValue() + " - " + standard.getMaximumValue() : "N/A") + ")",
                    "{\"testRecordId\":\"" + testRecordId + "\"}");
            log.warn("Test FAILED flag created for package {}, test record {}", request.packageId(), testRecordId);
        }

        // Run anomaly detection on the submitted test
        anomalyDetector.analyze(record);

        // Record trace event
        traceEventService.recordEvent(
                result == TestResult.PASS ? ActionType.TEST_PASSED : ActionType.TEST_FAILED,
                "PACKAGE", request.packageId(),
                testerUuid, "TESTING_AGENT",
                null, result.name(),
                null, null,
                request.qrId(), null
        );

        log.info("Test submitted: {} for package {} result={}", testRecordId, request.packageId(), result);
        return toResponse(record);
    }

    private TestResult evaluateTest(String measuredValue, StandardRequirement standard) {
        if (standard == null || measuredValue == null) {
            return TestResult.PASS; // No standard configured, assume pass
        }

        try {
            BigDecimal value = new BigDecimal(measuredValue);

            if (standard.getMinimumValue() != null && value.compareTo(standard.getMinimumValue()) < 0) {
                return TestResult.FAIL;
            }
            if (standard.getMaximumValue() != null && value.compareTo(standard.getMaximumValue()) > 0) {
                return TestResult.FAIL;
            }

            return TestResult.PASS;
        } catch (NumberFormatException e) {
            log.warn("Cannot parse measured value: {}", measuredValue);
            return TestResult.HOLD;
        }
    }

    private void updatePackageTestingStatus(Package pkg, TestResult result) {
        if (result == TestResult.FAIL) {
            pkg.setTestingStatus("FAILED");
            pkg.setQuarantined(true);
        } else if (result == TestResult.PASS) {
            pkg.setTestingStatus("PASSED");
        } else {
            pkg.setTestingStatus("ON_HOLD");
        }
        packageRepository.save(pkg);
    }

    public List<TestResultResponse> getTestHistory(String packageId) {
        return testRecordRepository.findByObjectTypeAndObjectIdOrderByTestedAtDesc("PACKAGE", packageId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TestResultResponse getTestRecord(String testRecordId) {
        TestRecord record = testRecordRepository.findByTestRecordId(testRecordId)
                .orElseThrow(() -> new EntityNotFoundException("TestRecord", testRecordId));
        return toResponse(record);
    }

    private TestResultResponse toResponse(TestRecord record) {
        return new TestResultResponse(
                record.getTestRecordId(),
                record.getObjectType(),
                record.getObjectId(),
                record.getTestProfileId(),
                record.getTestDefinitionId(),
                record.getTesterUuid(),
                record.getMeasurementSource(),
                record.getMeasuredValue(),
                record.getUnit(),
                record.getResult(),
                record.getStandardName(),
                record.getMinThreshold(),
                record.getMaxThreshold(),
                record.isMandatory(),
                record.getTestedAt(),
                record.isAnomalyFlag()
        );
    }
}