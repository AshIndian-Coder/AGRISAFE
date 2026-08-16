package com.agro.trace.aiml.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Mock AI integration for SIH prototype.
 * In production, replace with actual AI/ML model integration.
 */
@Service
@Slf4j
public class MockAiIntegrationPort implements AiIntegrationPort {

    @Override
    public FraudAnalysisResponse analyzeFraud(FraudAnalysisRequest request) {
        log.info("[AI MOCK] analyzeFraud: lot={}", request.lotId());
        return new FraudAnalysisResponse(
                0.05, // Low fraud score
                "LOW",
                new String[]{},
                false,
                "AI-ANALYSIS-" + UUID.randomUUID().toString().substring(0, 8)
        );
    }

    @Override
    public QualityAnalysisResponse analyzeQuality(QualityAnalysisRequest request) {
        log.info("[AI MOCK] analyzeQuality: product={}", request.productId());
        return new QualityAnalysisResponse(
                "A",
                0.92,
                new String[]{"Good color", "Consistent texture"},
                true
        );
    }

    @Override
    public ImageAnalysisResponse analyzeImage(ImageAnalysisRequest request) {
        log.info("[AI MOCK] analyzeImage: image={}, type={}", request.imageUrl(), request.analysisType());
        return new ImageAnalysisResponse(
                "NO_DEFECT",
                0.98,
                "GOOD",
                "AI-IMG-" + UUID.randomUUID().toString().substring(0, 8)
        );
    }
}