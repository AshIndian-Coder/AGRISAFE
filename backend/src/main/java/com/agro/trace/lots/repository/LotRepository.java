package com.agro.trace.lots.repository;

import com.agro.trace.common.domain.LotStatus;
import com.agro.trace.lots.domain.Lot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LotRepository extends JpaRepository<Lot, Long> {
    Optional<Lot> findByLotId(String lotId);
    Optional<Lot> findByUuid(String uuid);
    Page<Lot> findByFarmerUuidOrderByCreatedAtDesc(String farmerUuid, Pageable pageable);
    Page<Lot> findByCurrentCustodianUuidOrderByCreatedAtDesc(String custodianUuid, Pageable pageable);
    List<Lot> findByStatus(LotStatus status);
    List<Lot> findByFarmerUuid(String farmerUuid);
    boolean existsByLotId(String lotId);
}