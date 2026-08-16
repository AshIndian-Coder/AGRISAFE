package com.agro.trace.fraud.service;

import com.agro.trace.common.domain.FlagSeverity;
import com.agro.trace.common.dto.PagedResponse;
import com.agro.trace.common.exception.EntityNotFoundException;
import com.agro.trace.fraud.domain.Flag;
import com.agro.trace.fraud.repository.FlagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlagService {

    private final FlagRepository flagRepository;

    @Transactional
    public Flag createFlag(String flagType, FlagSeverity severity, String entityType, String entityId,
                           String actorUuid, String description, String evidenceJson) {
        String flagId = "FLG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Flag flag = new Flag();
        flag.setFlagId(flagId);
        flag.setFlagType(flagType);
        flag.setSeverity(severity);
        flag.setEntityType(entityType);
        flag.setEntityId(entityId);
        flag.setActorUuid(actorUuid);
        flag.setDescription(description);
        flag.setEvidenceJson(evidenceJson);
        flag.setStatus("OPEN");

        flag = flagRepository.save(flag);

        log.warn("Flag created: {} type={} entity={}:{} severity={}",
                flagId, flagType, entityType, entityId, severity);

        return flag;
    }

    @Transactional
    public Flag assignInvestigator(Long flagId, String investigatorUuid) {
        Flag flag = flagRepository.findById(flagId)
                .orElseThrow(() -> new EntityNotFoundException("Flag", String.valueOf(flagId)));
        flag.setAssignedInvestigatorUuid(investigatorUuid);
        flag.setStatus("INVESTIGATING");
        return flagRepository.save(flag);
    }

    @Transactional
    public Flag resolveFlag(Long flagId, String resolution) {
        Flag flag = flagRepository.findById(flagId)
                .orElseThrow(() -> new EntityNotFoundException("Flag", String.valueOf(flagId)));
        flag.setStatus("RESOLVED");
        flag.setResolution(resolution);
        flag.setResolvedAt(Instant.now());
        return flagRepository.save(flag);
    }

    public Flag getFlag(Long id) {
        return flagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flag", String.valueOf(id)));
    }

    public PagedResponse<Flag> getAllFlags(Pageable pageable) {
        Page<Flag> flags = flagRepository.findAllByOrderByCreatedAtDesc(pageable);
        return PagedResponse.from(flags);
    }

    public List<Flag> getFlagsByEntity(String entityType, String entityId) {
        return flagRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    public PagedResponse<Flag> getOpenFlags(Pageable pageable) {
        Page<Flag> flags = flagRepository.findByStatusOrderByCreatedAtDesc("OPEN", pageable);
        return PagedResponse.from(flags);
    }
}