package com.agrichain.audit.repository;

import com.agrichain.audit.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    Page<AuditLog> findByActorId(UUID actorId, Pageable pageable);

    Page<AuditLog> findByResourceTypeAndResourceId(String resourceType, UUID resourceId, Pageable pageable);

    Page<AuditLog> findByTraceId(String traceId, Pageable pageable);

    Page<AuditLog> findByTimestampBetween(Instant from, Instant to, Pageable pageable);

    Page<AuditLog> findByAction(String action, Pageable pageable);
}
