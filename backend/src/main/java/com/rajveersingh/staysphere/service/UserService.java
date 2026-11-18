package com.rajveersingh.staysphere.service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rajveersingh.staysphere.dto.RegisterRequest;
import com.rajveersingh.staysphere.dto.RegisterResponse;
import com.rajveersingh.staysphere.entity.User;
import com.rajveersingh.staysphere.enums.AccountStatus;
import com.rajveersingh.staysphere.enums.ApprovalStatus;
import com.rajveersingh.staysphere.enums.Role;
import com.rajveersingh.staysphere.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        if (request.getRole() != Role.TENANT && request.getRole() != Role.PG_OWNER) {
            throw new IllegalArgumentException("Invalid registration role");
        }

        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        if (repository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already registered");
        }

        if (request.getPassword().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Password exceeds the maximum allowed byte length");
        }

        User user = new User();

        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        if (request.getRole() == Role.TENANT) {
            user.setApprovalStatus(ApprovalStatus.APPROVED);
        } else {
            user.setApprovalStatus(ApprovalStatus.PENDING);
        }

        user.setAccountStatus(AccountStatus.ACTIVE);

        User savedUser = repository.save(user);

        String message = savedUser.getRole() == Role.TENANT ? "Registration successfull"
                : "Registration successful. Awaiting admin approval.";

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getPhoneNumber(),
                savedUser.getRole(),
                savedUser.getApprovalStatus(),
                message);
    }
}
