package com.agro.trace.qr.repository;

import com.agro.trace.common.domain.QrStatus;
import com.agro.trace.qr.domain.QrCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QrCredentialRepository extends JpaRepository<QrCredential, Long> {
    Optional<QrCredential> findByQrId(String qrId);
    List<QrCredential> findByObjectTypeAndObjectId(String objectType, String objectId);
    List<QrCredential> findByStatus(QrStatus status);
    boolean existsByQrId(String qrId);
}