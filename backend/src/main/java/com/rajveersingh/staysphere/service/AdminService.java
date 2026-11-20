package com.rajveersingh.staysphere.service;

import java.util.List;
import java.util.UUID;

import com.rajveersingh.staysphere.exception.BadRequestException;
import com.rajveersingh.staysphere.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rajveersingh.staysphere.dto.OwnerResponse;
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

    @Transactional(readOnly=true)
    public List<OwnerResponse> getAllOwners(){
        return repository.findByRole(Role.PG_OWNER)
            .stream()
            .map(this::mapToOwnerResponse)
            .toList();
    }

    private OwnerResponse mapToOwnerResponse(User user) {
    return new OwnerResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPhoneNumber(),
            user.getApprovalStatus(),
            user.getAccountStatus(),
            user.getCreatedAt()
    );
}

}
