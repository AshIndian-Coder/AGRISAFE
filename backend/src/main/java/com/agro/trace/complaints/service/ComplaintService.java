package com.agro.trace.complaints.service;

import com.agro.trace.common.dto.PagedResponse;
import com.agro.trace.common.exception.EntityNotFoundException;
import com.agro.trace.complaints.domain.Complaint;
import com.agro.trace.complaints.dto.ComplaintRequest;
import com.agro.trace.complaints.dto.ComplaintResponse;
import com.agro.trace.complaints.repository.ComplaintRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;

    @Transactional
    public ComplaintResponse registerComplaint(ComplaintRequest request, String complainantUuid, String role) {
        String complaintId = "CMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Complaint complaint = new Complaint();
        complaint.setComplaintId(complaintId);
        complaint.setComplainantUuid(complainantUuid);
        complaint.setComplainantRole(role);
        complaint.setCategory(request.category());
        complaint.setDescription(request.description());
        complaint.setRelatedLotId(request.relatedLotId());
        complaint.setRelatedOrganizationId(request.relatedOrganizationId());
        complaint.setStatus("PENDING");

        complaint = complaintRepository.save(complaint);
        return toResponse(complaint);
    }

    public PagedResponse<ComplaintResponse> getComplainantComplaints(String complainantUuid, Pageable pageable) {
        Page<Complaint> complaints = complaintRepository
                .findByComplainantUuidOrderByCreatedAtDesc(complainantUuid, pageable);
        return PagedResponse.from(complaints.map(this::toResponse));
    }

    public PagedResponse<ComplaintResponse> getAllComplaints(Pageable pageable) {
        Page<Complaint> complaints = complaintRepository.findAllByOrderByCreatedAtDesc(pageable);
        return PagedResponse.from(complaints.map(this::toResponse));
    }

    public ComplaintResponse getComplaint(String complaintId) {
        Complaint complaint = complaintRepository.findByComplaintId(complaintId)
                .orElseThrow(() -> new EntityNotFoundException("Complaint", complaintId));
        return toResponse(complaint);
    }

    @Transactional
    public ComplaintResponse resolveComplaint(String complaintId, String resolution, String officerUuid) {
        Complaint complaint = complaintRepository.findByComplaintId(complaintId)
                .orElseThrow(() -> new EntityNotFoundException("Complaint", complaintId));
        complaint.setStatus("RESOLVED");
        complaint.setResolution(resolution);
        complaint.setAssignedOfficerUuid(officerUuid);
        complaint.setResolvedAt(Instant.now());
        complaint = complaintRepository.save(complaint);
        return toResponse(complaint);
    }

    private ComplaintResponse toResponse(Complaint complaint) {
        return new ComplaintResponse(
                complaint.getComplaintId(),
                complaint.getComplainantUuid(),
                complaint.getComplainantRole(),
                complaint.getCategory(),
                complaint.getDescription(),
                complaint.getRelatedLotId(),
                complaint.getStatus(),
                complaint.getAssignedOfficerUuid(),
                complaint.getResolution(),
                complaint.getCreatedAt(),
                complaint.getResolvedAt()
        );
    }
}