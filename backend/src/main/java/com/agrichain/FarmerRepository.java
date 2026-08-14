package com.agrichain.farmer.repository;

import com.agrichain.farmer.entity.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FarmerRepository extends JpaRepository<Farmer, UUID> {

    @Query("SELECT f FROM Farmer f LEFT JOIN FETCH f.user WHERE f.user.id = :userId AND f.deletedAt IS NULL")
    Optional<Farmer> findByUserIdWithUser(UUID userId);

    Optional<Farmer> findByUserIdAndDeletedAtIsNull(UUID userId);

    Optional<Farmer> findByFarmerIdNumberAndDeletedAtIsNull(String farmerIdNumber);

    boolean existsByUserIdAndDeletedAtIsNull(UUID userId);
}
