package com.agrichain.identity.repository;

import com.agrichain.identity.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {

    @Query("SELECT s FROM Session s WHERE s.id = :id AND s.isRevoked = false AND s.expiresAt > :now")
    Optional<Session> findValidSession(UUID id, Instant now);

    @Query("SELECT s FROM Session s WHERE s.user.id = :userId AND s.isRevoked = false AND s.expiresAt > :now ORDER BY s.lastUsedAt DESC")
    List<Session> findActiveSessionsByUserId(UUID userId, Instant now);

    @Modifying
    @Query("UPDATE Session s SET s.isRevoked = true, s.revokedAt = :now, s.revokedReason = :reason WHERE s.user.id = :userId AND s.isRevoked = false")
    int revokeAllSessionsForUser(UUID userId, Instant now, String reason);

    @Modifying
    @Query("DELETE FROM Session s WHERE s.expiresAt < :cutoff")
    int deleteExpiredSessions(Instant cutoff);
}
