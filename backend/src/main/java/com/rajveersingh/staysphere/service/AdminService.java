package com.rajveersingh.staysphere.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rajveersingh.staysphere.dto.PendingOwnerResponse;
import com.rajveersingh.staysphere.entity.User;
import com.rajveersingh.staysphere.enums.ApprovalStatus;
import com.rajveersingh.staysphere.enums.Role;
import com.rajveersingh.staysphere.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository repository;

    @Transactional(readOnly = true)
    public List<PendingOwnerResponse> getPendingOwners() {
        List<User> users = repository.findByRoleAndApprovalStatus(Role.PG_OWNER, ApprovalStatus.PENDING);

        return users.stream()
                .map(user -> new PendingOwnerResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getPhoneNumber(),
                        user.getApprovalStatus(),
                        user.getCreatedAt()))
                .toList();
    }

}
