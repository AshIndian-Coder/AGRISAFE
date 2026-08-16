package com.agro.trace.testing.dto;

import com.agro.trace.common.domain.TestResult;
import java.time.Instant;

public record TestResultResponse(
        String testRecordId,
        String objectType,
        String objectId,
        Long testProfileId,
        Long testDefinitionId,
        String testerUuid,
        String measurementSource,
        String measuredValue,
        String unit,
        TestResult result,
        String standardName,
        String minThreshold,
        String maxThreshold,
        boolean mandatory,
        Instant testedAt,
        boolean anomalyFlag
) {}