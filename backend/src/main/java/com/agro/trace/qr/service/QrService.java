package com.agro.trace.qr.service;

import com.agro.trace.common.domain.FlagSeverity;
import com.agro.trace.common.domain.QrStatus;
import com.agro.trace.common.exception.BusinessException;
import com.agro.trace.common.exception.EntityNotFoundException;
import com.agro.trace.qr.domain.QrCredential;
import com.agro.trace.qr.domain.QrScanHistory;
import com.agro.trace.qr.repository.QrCredentialRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import com.agro.trace.qr.repository.QrScanHistoryRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class QrService {
    private static final long ROTATION_INTERVAL_SECONDS = 30L;
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final QrCredentialRepository qrCredentialRepository;
    private final QrScanHistoryRepository qrScanHistoryRepository;
    private final com.agro.trace.fraud.service.FlagService flagService;

    @Transactional
    public String generateQr(String objectType, String objectId, String stage) {
        return generateQr(objectType, objectId, stage, null);
    }

    @Transactional
    public String generateQr(String objectType, String objectId, String stage, String siblingGroupId) {
        String qrId = "QR-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();

        QrCredential qr = new QrCredential();
        qr.setQrId(qrId);
        qr.setObjectType(objectType);
        qr.setObjectId(objectId);
        qr.setStage(stage);
        qr.setStatus(QrStatus.ACTIVE);
        qr.setIssuedAt(Instant.now());
        qr.setDynamicSecret(UUID.randomUUID().toString());
        qr.setSiblingGroupId(siblingGroupId);

        qrCredentialRepository.save(qr);

        log.debug("QR generated: {} for {}:{} (sibling: {})", qrId, objectType, objectId, siblingGroupId);
        return qrId;
    }

    public String getCurrentRotatingCode(String qrId) {

        QrCredential qr = getQrDetails(qrId);

        if (qr.getStatus() != QrStatus.ACTIVE) {
            throw new BusinessException(
                    "QR_INVALID_STATE",
                    "QR is not active. Status: " + qr.getStatus(),
                    400
            );
        }

        long timeWindow =
                Instant.now().getEpochSecond()
                        / ROTATION_INTERVAL_SECONDS;

        return generateCode(qr, timeWindow);
    }

    private String generateCode(QrCredential qr, long timeWindow) {

        String payload = qr.getQrId() + ":" + timeWindow;

        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);

            SecretKeySpec secretKey = new SecretKeySpec(
                    qr.getDynamicSecret().getBytes(StandardCharsets.UTF_8),
                    HMAC_ALGORITHM
            );

            mac.init(secretKey);

            byte[] digest = mac.doFinal(
                    payload.getBytes(StandardCharsets.UTF_8)
            );

            String encoded = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(digest);

            return encoded
                    .replace("-", "")
                    .replace("_", "")
                    .substring(0, 6)
                    .toUpperCase();

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Unable to generate rotating QR code",
                    e
            );
        }
    }

    private boolean isValidRotatingCode(
            QrCredential qr,
            String suppliedCode
    ) {

        long currentWindow =
                Instant.now().getEpochSecond()
                        / ROTATION_INTERVAL_SECONDS;

        String currentCode =
                generateCode(qr, currentWindow);

        if (MessageDigest.isEqual(
                currentCode.getBytes(StandardCharsets.UTF_8),
                suppliedCode.getBytes(StandardCharsets.UTF_8)
        )) {
            return true;
        }

        String previousCode =
                generateCode(qr, currentWindow - 1);

        return MessageDigest.isEqual(
                previousCode.getBytes(StandardCharsets.UTF_8),
                suppliedCode.getBytes(StandardCharsets.UTF_8)
        );
    }

    private String hashCode(String value) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    value.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder result = new StringBuilder();

            for (byte b : hash) {
                result.append(String.format("%02x", b));
            }

            return result.toString();

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Unable to hash rotating QR code",
                    e
            );
        }
    }

    @Transactional
    public QrCredential consumeQr(String qrId, String consumedBy, BigDecimal latitude, BigDecimal longitude) {
        QrCredential qr = qrCredentialRepository.findByQrId(qrId)
                .orElseThrow(() -> new EntityNotFoundException("QR", qrId));

        if (qr.getStatus() != QrStatus.ACTIVE) {
            // Create anomaly flag for replay attempt
            if (qr.getStatus() == QrStatus.CONSUMED) {
                log.warn("QR replay attempt detected! QR: {} already consumed by {}", qrId, qr.getConsumedBy());
                flagService.createFlag("QR_REPLAY_ATTEMPT", FlagSeverity.HIGH,
                        "QR", qrId, consumedBy,
                        "QR already consumed by " + qr.getConsumedBy() + " at " + qr.getConsumedAt(),
                        "{\"qrId\":\"" + qrId + "\",\"previousConsumer\":\"" + qr.getConsumedBy() + "\"}");
                throw new BusinessException("QR_ALREADY_CONSUMED",
                        "QR has already been consumed. This incident has been logged.", 409);
            }
            throw new BusinessException("QR_INVALID_STATE",
                    "QR is not in active state. Current state: " + qr.getStatus(), 400);
        }

        if (qr.getExpiresAt() != null && qr.getExpiresAt().isBefore(Instant.now())) {
            qr.setStatus(QrStatus.EXPIRED);
            qrCredentialRepository.save(qr);
            throw new BusinessException("QR_EXPIRED", "QR has expired", 400);
        }

        qr.setStatus(QrStatus.CONSUMED);
        qr.setConsumedAt(Instant.now());
        qr.setConsumedBy(consumedBy);
        qr.setConsumedLatitude(latitude);
        qr.setConsumedLongitude(longitude);

        qr = qrCredentialRepository.save(qr);
        log.info("QR consumed: {} by {}", qrId, consumedBy);
        return qr;
    }
    @Transactional
    public QrCredential verifyAndConsumeRotatingQr(
            String qrId,
            String rotatingCode,
            String consumedBy,
            String consumedByRole,
            BigDecimal latitude,
            BigDecimal longitude
    ) {

        QrCredential qr = qrCredentialRepository
                .findByQrId(qrId)
                .orElseThrow(
                        () -> new EntityNotFoundException("QR", qrId)
                );

        // Replay attempt
        if (qr.getStatus() == QrStatus.CONSUMED) {

            String evidence = String.format(
                    """
                    {
                      "qrId":"%s",
                      "attemptedBy":"%s",
                      "attemptedRole":"%s",
                      "attemptedAt":"%s",
                      "attemptedLatitude":"%s",
                      "attemptedLongitude":"%s",
                      "attemptedCodeHash":"%s",
                      "originalConsumer":"%s",
                      "originalConsumedAt":"%s",
                      "originalLatitude":"%s",
                      "originalLongitude":"%s"
                    }
                    """,
                    qrId,
                    consumedBy,
                    consumedByRole,
                    Instant.now(),
                    latitude,
                    longitude,
                    rotatingCode == null ? null : hashCode(rotatingCode),
                    qr.getConsumedBy(),
                    qr.getConsumedAt(),
                    qr.getConsumedLatitude(),
                    qr.getConsumedLongitude()
            );

            flagService.createFlag(
                    "QR_REPLAY_ATTEMPT",
                    FlagSeverity.HIGH,
                    "QR",
                    qrId,
                    consumedBy,
                    "Attempted reuse of an already consumed QR",
                    evidence
            );

            throw new BusinessException(
                    "QR_ALREADY_CONSUMED",
                    "QR has already been consumed. Replay attempt has been flagged.",
                    409
            );
        }

        if (qr.getStatus() != QrStatus.ACTIVE) {
            throw new BusinessException(
                    "QR_INVALID_STATE",
                    "QR is not active. Current state: " + qr.getStatus(),
                    400
            );
        }

        if (rotatingCode == null || rotatingCode.isBlank()) {
            throw new BusinessException(
                    "ROTATING_CODE_REQUIRED",
                    "Rotating QR code is required.",
                    400
            );
        }

        if (!isValidRotatingCode(qr, rotatingCode)) {

            flagService.createFlag(
                    "QR_INVALID_ROTATING_CODE",
                    FlagSeverity.HIGH,
                    "QR",
                    qrId,
                    consumedBy,
                    "Invalid 30-second rotating QR credential",
                    String.format(
                            """
                            {
                              "qrId":"%s",
                              "attemptedBy":"%s",
                              "attemptedRole":"%s",
                              "attemptedAt":"%s",
                              "latitude":"%s",
                              "longitude":"%s"
                            }
                            """,
                            qrId,
                            consumedBy,
                            consumedByRole,
                            Instant.now(),
                            latitude,
                            longitude
                    )
            );

            throw new BusinessException(
                    "QR_CODE_INVALID",
                    "Rotating QR code is invalid or expired.",
                    409
            );
        }

        // Freeze evidence of the successful scan
        QrScanHistory scan = new QrScanHistory();

        scan.setQrId(qr.getQrId());
        scan.setRotatingCodeHash(hashCode(rotatingCode));
        scan.setScanTimestamp(Instant.now());
        scan.setScannedByUuid(consumedBy);
        scan.setScannedByRole(consumedByRole);
        scan.setLatitude(latitude);
        scan.setLongitude(longitude);
        scan.setStage(qr.getStage());
        scan.setObjectType(qr.getObjectType());
        scan.setObjectId(qr.getObjectId());
        scan.setResult("SUCCESS");

        qrScanHistoryRepository.save(scan);

        // Consume the physical QR
        qr.setStatus(QrStatus.CONSUMED);
        qr.setConsumedAt(Instant.now());
        qr.setConsumedBy(consumedBy);
        qr.setConsumedLatitude(latitude);
        qr.setConsumedLongitude(longitude);

        return qrCredentialRepository.save(qr);
    }

    public QrCredential validateQrStatus(String qrId) {
        QrCredential qr = qrCredentialRepository.findByQrId(qrId)
                .orElseThrow(() -> new EntityNotFoundException("QR", qrId));

        if (qr.getStatus() != QrStatus.ACTIVE) {
            throw new BusinessException("QR_INVALID_STATE",
                    "QR is not active. Status: " + qr.getStatus(), 400);
        }
        return qr;
    }

    public QrCredential getQrDetails(String qrId) {
        return qrCredentialRepository.findByQrId(qrId)
                .orElseThrow(() -> new EntityNotFoundException("QR", qrId));
    }

    public List<QrCredential> getSiblingQrs(String siblingGroupId) {
        return qrCredentialRepository.findBySiblingGroupId(siblingGroupId);
    }

    public SiblingValidationResult validateSiblingGroup(String siblingGroupId, String scannedQrId) {
        List<QrCredential> siblings = getSiblingQrs(siblingGroupId);
        if (siblings.isEmpty()) {
            return new SiblingValidationResult(true, 0, 0, List.of());
        }
        long consumed = siblings.stream()
                .filter(q -> q.getStatus() == QrStatus.CONSUMED)
                .count();
        List<String> missingIds = siblings.stream()
                .filter(q -> q.getStatus() == QrStatus.ACTIVE)
                .map(QrCredential::getQrId)
                .filter(id -> !id.equals(scannedQrId))
                .toList();
        boolean allConsumed = missingIds.isEmpty();
        return new SiblingValidationResult(allConsumed, siblings.size(), consumed, missingIds);
    }

    public record SiblingValidationResult(
            boolean allConsumed,
            int totalSiblings,
            long consumedCount,
            List<String> missingQrIds
    ) {}

    @Transactional
    public void revokeQr(String qrId) {
        QrCredential qr = qrCredentialRepository.findByQrId(qrId)
                .orElseThrow(() -> new EntityNotFoundException("QR", qrId));
        qr.setStatus(QrStatus.REVOKED);
        qrCredentialRepository.save(qr);
        log.warn("QR revoked: {}", qrId);
    }
}