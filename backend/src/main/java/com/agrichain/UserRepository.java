package com.agrichain.identity.repository;

import com.agrichain.identity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByPhoneAndDeletedAtIsNull(String phone);

    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    boolean existsByPhoneAndDeletedAtIsNull(String phone);

    boolean existsByEmailAndDeletedAtIsNull(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.organization WHERE u.id = :id AND u.deletedAt IS NULL")
    Optional<User> findByIdWithOrganization(UUID id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.organization WHERE u.phone = :phone AND u.deletedAt IS NULL")
    Optional<User> findByPhoneWithOrganization(String phone);
}
