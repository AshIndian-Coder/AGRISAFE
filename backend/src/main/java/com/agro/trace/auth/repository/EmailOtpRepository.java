package com.agro.trace.auth.repository;

import com.agro.trace.auth.domain.EmailOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {

    @Query("SELECT o FROM EmailOtp o WHERE o.email = :email AND o.purpose = :purpose AND o.verified = false ORDER BY o.createdAt DESC LIMIT 1")
    Optional<EmailOtp> findLatestUnverified(@Param("email") String email, @Param("purpose") String purpose);

    @Modifying
    @Query("DELETE FROM EmailOtp o WHERE o.email = :email AND o.purpose = :purpose")
    void deleteByPurpose(@Param("email") String email, @Param("purpose") String purpose);
}
