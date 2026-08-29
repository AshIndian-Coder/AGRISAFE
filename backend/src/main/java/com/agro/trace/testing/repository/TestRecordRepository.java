package com.agro.trace.testing.repository;

import com.agro.trace.testing.domain.TestRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestRecordRepository extends JpaRepository<TestRecord, Long> {
    Optional<TestRecord> findByTestRecordId(String testRecordId);
    List<TestRecord> findByObjectTypeAndObjectIdOrderByTestedAtDesc(String objectType, String objectId);
    List<TestRecord> findByTesterUuidOrderByTestedAtDesc(String testerUuid);
    List<TestRecord> findByObjectTypeAndObjectIdAndResult(String objectType, String objectId, String result);
}