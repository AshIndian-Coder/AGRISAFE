package com.agro.trace.aiml.service;

/**
 * AI/ML Integration Boundary.
 * 
 * This interface defines the contract for AI/ML integration.
 * In the SIH prototype, this is a mock/stub implementation.
 * In production, this would connect to AI/ML models for:
 * - Fraud detection
 * - Quality analysis
 * - Image analysis
 * - Anomaly detection
 * 
 * DO NOT implement actual AI/ML models - only the integration boundary.
 */

public interface AiIntegrationPort {

    /**
     * Analyze test data for fraud/anomaly patterns.
     */
    FraudAnalysisResponse analyzeFraud(FraudAnalysisRequest request);

    /**
     * Analyze product quality based on test data.
     */
    QualityAnalysisResponse analyzeQuality(QualityAnalysisRequest request);

    /**
     * Analyze product images for quality assessment.
     */
    ImageAnalysisResponse analyzeImage(ImageAnalysisRequest request);
}

// Request/Response DTOs

record FraudAnalysisRequest(
        String lotId,
        String packageId,
        String testerUuid,
        Object testData,
        String deviceId,
        Object historicalData
) {}

record FraudAnalysisResponse(
        double fraudScore,
        String riskLevel,
        String[] anomalies,
        boolean recommendedAction,
        String analysisId
) {}

record QualityAnalysisRequest(
        String productId,
        String varietyId,
        Object measurements,
        Object standardThresholds
) {}

record QualityAnalysisResponse(
        String qualityGrade,
        double qualityScore,
        String[] observations,
        boolean recommendedAction
) {}

record ImageAnalysisRequest(
        String imageUrl,
        String productType,
        String analysisType
) {}

record ImageAnalysisResponse(
        String defectType,
        double confidenceScore,
        String qualityEstimate,
        String analysisId
) {}