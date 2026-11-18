package com.rajveersingh.staysphere.service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rajveersingh.staysphere.dto.LoginRequest;
import com.rajveersingh.staysphere.dto.LoginResponse;
import com.rajveersingh.staysphere.dto.LoginResult;
import com.rajveersingh.staysphere.dto.RegisterRequest;
import com.rajveersingh.staysphere.dto.RegisterResponse;
import com.rajveersingh.staysphere.entity.User;
import com.rajveersingh.staysphere.enums.AccountStatus;
import com.rajveersingh.staysphere.enums.ApprovalStatus;
import com.rajveersingh.staysphere.enums.Role;
import com.rajveersingh.staysphere.exception.BadRequestException;
import com.rajveersingh.staysphere.exception.InvalidCredentialsException;
import com.rajveersingh.staysphere.exception.ResourceAlreadyExistsException;
import com.rajveersingh.staysphere.repository.UserRepository;
import com.rajveersingh.staysphere.security.JwtService;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        if (request.getRole() != Role.TENANT && request.getRole() != Role.PG_OWNER) {
            throw new BadRequestException("Invalid registration role");
        }

        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        if (repository.existsByEmail(email)) {
            throw new ResourceAlreadyExistsException("Email is already registered");
        }

        if (request.getPassword().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new BadRequestException("Password exceeds the maximum allowed byte length");
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

        String message = savedUser.getRole() == Role.TENANT ? "Registration successful"
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

    @Transactional(readOnly = true)
    public LoginResult login(LoginRequest request) {

        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        User user = repository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("Your account is suspended");
        }

        if (user.getRole() == Role.PG_OWNER && user.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new BadRequestException("Your PG owner account is awaiting approval or has been rejected");
        }

        String token = jwtService.generateToken(user.getId());

        LoginResponse response = new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getApprovalStatus(),
                user.getAccountStatus(),
                "Login successful");

        return new LoginResult(response, token);
    }
}
