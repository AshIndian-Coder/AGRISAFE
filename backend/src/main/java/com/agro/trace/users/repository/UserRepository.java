package com.agro.trace.users.repository;

import com.agro.trace.users.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUuid(String uuid);

    Optional<User> findByAadhaarReference(String aadhaarReference);

    Optional<User> findByPfReference(String pfReference);

    Optional<User> findByEmployeeId(String employeeId);

    Optional<User> findByGstNumber(String gstNumber);

    Optional<User> findByMobileNumber(String mobileNumber);

    boolean existsByAadhaarReference(String aadhaarReference);

    boolean existsByPfReference(String pfReference);

    boolean existsByEmployeeId(String employeeId);

    boolean existsByGstNumber(String gstNumber);

    boolean existsByPanNumber(String panNumber);

    @Query("SELECT u FROM User u WHERE u.aadhaarReference = :ref OR u.pfReference = :ref OR u.employeeId = :ref OR u.mobileNumber = :ref")
    Optional<User> findByIdentity(@Param("ref") String identityRef);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.uuid = :uuid")
    Optional<User> findByUuidWithRoles(@Param("uuid") String uuid);
}