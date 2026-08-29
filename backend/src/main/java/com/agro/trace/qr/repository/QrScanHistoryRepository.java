package com.agro.trace.qr.repository;

import com.agro.trace.qr.domain.QrScanHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QrScanHistoryRepository
        extends JpaRepository<QrScanHistory, Long> {

    List<QrScanHistory> findByQrIdOrderByScanTimestampDesc(String qrId);
}