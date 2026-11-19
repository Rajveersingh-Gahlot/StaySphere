package com.rajveersingh.staysphere.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rajveersingh.staysphere.entity.User;
import com.rajveersingh.staysphere.enums.ApprovalStatus;
import com.rajveersingh.staysphere.enums.Role;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    List<User> findByRoleAndApprovalStatus(Role role, ApprovalStatus approvalStatus);
}
