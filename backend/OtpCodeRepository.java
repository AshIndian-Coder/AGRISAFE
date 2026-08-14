package com.agrichain.identity.repository;

import com.agrichain.identity.entity.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, UUID> {

    @Query("SELECT o FROM OtpCode o WHERE o.phone = :phone AND o.purpose = :purpose " +
           "AND o.isUsed = false AND o.expiresAt > :now ORDER BY o.createdAt DESC LIMIT 1")
    Optional<OtpCode> findLatestValidOtp(String phone, String purpose, Instant now);

    @Query("SELECT COUNT(o) > 0 FROM OtpCode o WHERE o.phone = :phone AND o.purpose = :purpose " +
           "AND o.isUsed = false AND o.createdAt > :since")
    boolean hasRecentOtp(String phone, String purpose, Instant since);
}
