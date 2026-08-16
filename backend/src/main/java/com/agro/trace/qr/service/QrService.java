package com.agro.trace.qr.service;

import com.agro.trace.common.domain.FlagSeverity;
import com.agro.trace.common.domain.QrStatus;
import com.agro.trace.common.exception.BusinessException;
import com.agro.trace.common.exception.EntityNotFoundException;
import com.agro.trace.qr.domain.QrCredential;
import com.agro.trace.qr.repository.QrCredentialRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class QrService {

    private final QrCredentialRepository qrCredentialRepository;
    private final com.agro.trace.fraud.service.FlagService flagService;

    @Transactional
    public String generateQr(String objectType, String objectId, String stage) {
        String qrId = "QR-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();

        QrCredential qr = new QrCredential();
        qr.setQrId(qrId);
        qr.setObjectType(objectType);
        qr.setObjectId(objectId);
        qr.setStage(stage);
        qr.setStatus(QrStatus.ACTIVE);
        qr.setIssuedAt(Instant.now());
        qr.setDynamicSecret(UUID.randomUUID().toString());

        qrCredentialRepository.save(qr);

        log.debug("QR generated: {} for {}:{}", qrId, objectType, objectId);
        return qrId;
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

    @Transactional
    public void revokeQr(String qrId) {
        QrCredential qr = qrCredentialRepository.findByQrId(qrId)
                .orElseThrow(() -> new EntityNotFoundException("QR", qrId));
        qr.setStatus(QrStatus.REVOKED);
        qrCredentialRepository.save(qr);
        log.warn("QR revoked: {}", qrId);
    }
}