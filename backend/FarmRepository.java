package com.agrichain.farm.repository;

import com.agrichain.farm.entity.Farm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FarmRepository extends JpaRepository<Farm, UUID> {

    @Query("SELECT f FROM Farm f " +
           "LEFT JOIN FETCH f.farmer fr " +
           "LEFT JOIN FETCH fr.user " +
           "WHERE f.id = :id AND f.deletedAt IS NULL")
    Optional<Farm> findByIdWithDetails(UUID id);

    Page<Farm> findByFarmerIdAndDeletedAtIsNull(UUID farmerId, Pageable pageable);

    Page<Farm> findByOrganizationIdAndDeletedAtIsNull(UUID organizationId, Pageable pageable);

    Page<Farm> findByFarmerIdAndIsActiveAndDeletedAtIsNull(UUID farmerId, Boolean isActive, Pageable pageable);

    long countByFarmerIdAndDeletedAtIsNull(UUID farmerId);
}
