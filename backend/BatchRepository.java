package com.agrichain.batch.repository;

import com.agrichain.batch.entity.Batch;
import com.agrichain.common.enums.BatchStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BatchRepository extends JpaRepository<Batch, UUID>, JpaSpecificationExecutor<Batch> {

    @Query("SELECT b FROM Batch b " +
           "LEFT JOIN FETCH b.product " +
           "LEFT JOIN FETCH b.farm " +
           "LEFT JOIN FETCH b.farmer f " +
           "LEFT JOIN FETCH f.user " +
           "WHERE b.id = :id AND b.deletedAt IS NULL")
    Optional<Batch> findByIdWithDetails(UUID id);

    Optional<Batch> findByBatchCodeAndDeletedAtIsNull(String batchCode);

    Optional<Batch> findByClientOperationIdAndDeletedAtIsNull(String clientOperationId);

    Page<Batch> findByFarmerIdAndDeletedAtIsNull(UUID farmerId, Pageable pageable);

    Page<Batch> findByOrganizationIdAndDeletedAtIsNull(UUID organizationId, Pageable pageable);

    Page<Batch> findByStatusAndDeletedAtIsNull(BatchStatus status, Pageable pageable);

    @Query("SELECT b FROM Batch b WHERE b.deletedAt IS NULL " +
           "AND (:status IS NULL OR b.status = :status) " +
           "AND (:farmerId IS NULL OR b.farmer.id = :farmerId) " +
           "AND (:farmId IS NULL OR b.farm.id = :farmId) " +
           "AND (:productId IS NULL OR b.product.id = :productId) " +
           "AND (:dateFrom IS NULL OR b.harvestDate >= :dateFrom) " +
           "AND (:dateTo IS NULL OR b.harvestDate <= :dateTo)")
    Page<Batch> findWithFilters(
            BatchStatus status,
            UUID farmerId,
            UUID farmId,
            UUID productId,
            Instant dateFrom,
            Instant dateTo,
            Pageable pageable
    );

    boolean existsByBatchCode(String batchCode);
}
