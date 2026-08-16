package com.agro.trace.audit.service;

import com.agro.trace.audit.domain.AuditLog;
import com.agro.trace.audit.repository.AuditLogRepository;
import com.agro.trace.common.domain.ActionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Async
    public void recordAudit(ActionType action, String actorUuid, String actorRole,
                            String entityType, String entityId, String details,
                            String ipAddress, String dataClassification) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setActorUuid(actorUuid);
            auditLog.setActorRole(actorRole);
            auditLog.setAction(action);
            auditLog.setEntityType(entityType);
            auditLog.setEntityId(entityId);
            auditLog.setDetails(details);
            auditLog.setTimestamp(Instant.now());
            auditLog.setIpAddress(ipAddress);
            auditLog.setTraceId(MDC.get("correlationId"));
            auditLog.setCorrelationId(MDC.get("correlationId"));
            auditLog.setDataClassification(dataClassification != null ? dataClassification : "OPERATIONAL");

            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to record audit log: {}", e.getMessage());
        }
    }
}