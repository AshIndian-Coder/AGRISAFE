package com.agrichain.audit.service;

import com.agrichain.audit.entity.AuditLog;
import com.agrichain.audit.repository.AuditLogRepository;
import com.agrichain.security.AuthenticatedUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Audit logging service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @Async
    public void logCreate(String resourceType, UUID resourceId, Object newState) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .actorId(getCurrentUserId())
                    .actorOrganizationId(getCurrentOrganizationId())
                    .action("CREATE")
                    .resourceType(resourceType)
                    .resourceId(resourceId)
                    .newState(toJson(newState))
                    .result("SUCCESS")
                    .build();
            
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to create audit log", e);
        }
    }

    @Async
    public void logUpdate(String resourceType, UUID resourceId, Object previousState, Object newState, String reason) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .actorId(getCurrentUserId())
                    .actorOrganizationId(getCurrentOrganizationId())
                    .action("UPDATE")
                    .resourceType(resourceType)
                    .resourceId(resourceId)
                    .previousState(toJson(previousState))
                    .newState(toJson(newState))
                    .reason(reason)
                    .result("SUCCESS")
                    .build();
            
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to create audit log", e);
        }
    }

    @Async
    public void logDelete(String resourceType, UUID resourceId, Object previousState, String reason) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .actorId(getCurrentUserId())
                    .actorOrganizationId(getCurrentOrganizationId())
                    .action("DELETE")
                    .resourceType(resourceType)
                    .resourceId(resourceId)
                    .previousState(toJson(previousState))
                    .reason(reason)
                    .result("SUCCESS")
                    .build();
            
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to create audit log", e);
        }
    }

    @Async
    public void logSecurityEvent(String action, String resourceType, UUID resourceId, String result, String errorCode) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .actorId(getCurrentUserId())
                    .actorOrganizationId(getCurrentOrganizationId())
                    .action(action)
                    .resourceType(resourceType)
                    .resourceId(resourceId)
                    .result(result)
                    .errorCode(errorCode)
                    .build();
            
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to create security audit log", e);
        }
    }

    private UUID getCurrentUserId() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof AuthenticatedUser) {
                return ((AuthenticatedUser) principal).getUserId();
            }
        } catch (Exception e) {
            // Not authenticated
        }
        return null;
    }

    private UUID getCurrentOrganizationId() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof AuthenticatedUser) {
                return ((AuthenticatedUser) principal).getOrganizationId();
            }
        } catch (Exception e) {
            // Not authenticated
        }
        return null;
    }

    private String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return null;
        }
    }
}
