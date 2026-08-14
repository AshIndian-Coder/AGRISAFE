package com.agrichain.handover.repository;

import com.agrichain.common.enums.HandoverStatus;
import com.agrichain.handover.entity.Handover;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HandoverRepository extends JpaRepository<Handover, UUID> {

    @Query("SELECT h FROM Handover h " +
           "LEFT JOIN FETCH h.batch " +
           "LEFT JOIN FETCH h.fromOrganization " +
           "LEFT JOIN FETCH h.toOrganization " +
           "WHERE h.id = :id")
    Optional<Handover> findByIdWithDetails(UUID id);

    Optional<Handover> findByIdempotencyKey(String idempotencyKey);

    Page<Handover> findByBatchId(UUID batchId, Pageable pageable);

    Page<Handover> findByFromOrganizationIdAndStatus(UUID orgId, HandoverStatus status, Pageable pageable);

    Page<Handover> findByToOrganizationIdAndStatus(UUID orgId, HandoverStatus status, Pageable pageable);

    @Query("SELECT h FROM Handover h WHERE h.batch.id = :batchId AND h.status IN ('INITIATED', 'PENDING_ACCEPTANCE', 'IN_TRANSIT')")
    Optional<Handover> findActiveHandoverForBatch(UUID batchId);
}
