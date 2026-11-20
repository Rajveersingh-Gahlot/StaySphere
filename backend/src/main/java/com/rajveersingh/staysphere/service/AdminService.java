package com.rajveersingh.staysphere.service;

import java.util.List;
import java.util.UUID;

import com.rajveersingh.staysphere.exception.BadRequestException;
import com.rajveersingh.staysphere.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rajveersingh.staysphere.dto.AdminDashboardResponse;
import com.rajveersingh.staysphere.dto.OwnerResponse;
import com.rajveersingh.staysphere.entity.User;
import com.rajveersingh.staysphere.enums.AccountStatus;
import com.rajveersingh.staysphere.enums.ApprovalStatus;
import com.rajveersingh.staysphere.enums.Role;
import com.rajveersingh.staysphere.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository repository;

    @Transactional(readOnly = true)
    public List<OwnerResponse> getPendingOwners() {
        List<User> users = repository.findByRoleAndApprovalStatus(Role.PG_OWNER, ApprovalStatus.PENDING);

        return users.stream()
                .map(this::mapToOwnerResponse)
                .toList();
    }

    @Transactional
    public void approveOwner(UUID id) {

        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != Role.PG_OWNER) {
            throw new BadRequestException("User is not a PG Owner");
        }

        if (user.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw new BadRequestException("PG Owner is not pending approval");
        }

        user.setApprovalStatus(ApprovalStatus.APPROVED);
    }

    @Transactional
    public void rejectOwner(UUID id) {

        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != Role.PG_OWNER) {
            throw new BadRequestException("User is not a PG Owner");
        }

        if (user.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw new BadRequestException("PG Owner is not pending approval");
        }

        user.setApprovalStatus(ApprovalStatus.REJECTED);
    }

    @Transactional(readOnly = true)
    public List<OwnerResponse> getAllOwners() {
        return repository.findByRole(Role.PG_OWNER)
                .stream()
                .map(this::mapToOwnerResponse)
                .toList();
    }

    @Transactional
    public void suspendOwner(UUID id) {

        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != Role.PG_OWNER) {
            throw new BadRequestException("User is not a PG Owner");
        }

        if (user.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new BadRequestException("Only approved PG Owners can be suspended");
        }

        if (user.getAccountStatus() == AccountStatus.SUSPENDED) {
            throw new BadRequestException("PG Owner is already suspended");
        }

        user.setAccountStatus(AccountStatus.SUSPENDED);
    }

    @Transactional
    public void activateOwner(UUID id) {

        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != Role.PG_OWNER) {
            throw new BadRequestException("User is not a PG Owner");
        }

        if (user.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new BadRequestException("Only approved PG Owners can be activated");
        }

        if (user.getAccountStatus() != AccountStatus.SUSPENDED) {
            throw new BadRequestException("PG Owner is not suspended");
        }

        user.setAccountStatus(AccountStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboardSummary() {

        return new AdminDashboardResponse(
                repository.countByRole(Role.PG_OWNER),
                repository.countByRoleAndApprovalStatus(
                        Role.PG_OWNER, ApprovalStatus.PENDING),
                repository.countByRoleAndApprovalStatus(
                        Role.PG_OWNER, ApprovalStatus.APPROVED),
                repository.countByRoleAndApprovalStatus(
                        Role.PG_OWNER, ApprovalStatus.REJECTED),
                repository.countByRoleAndAccountStatus(
                        Role.PG_OWNER, AccountStatus.SUSPENDED));
    }

    private OwnerResponse mapToOwnerResponse(User user) {
        return new OwnerResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getApprovalStatus(),
                user.getAccountStatus(),
                user.getCreatedAt());
    }

}
