package com.agro.trace.complaints.repository;

import com.agro.trace.complaints.domain.Complaint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    Optional<Complaint> findByComplaintId(String complaintId);
    Page<Complaint> findByComplainantUuidOrderByCreatedAtDesc(String complainantUuid, Pageable pageable);
    Page<Complaint> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Complaint> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);
}