package com.agro.trace.audit.repository;

import com.agro.trace.audit.domain.AuditLog;
import com.agro.trace.common.domain.ActionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    Page<AuditLog> findByActorUuidOrderByTimestampDesc(String actorUuid, Pageable pageable);
    Page<AuditLog> findByActionOrderByTimestampDesc(ActionType action, Pageable pageable);
    List<AuditLog> findByEntityTypeAndEntityId(String entityType, String entityId);
}