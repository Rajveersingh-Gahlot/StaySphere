package com.rajveersingh.staysphere.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.rajveersingh.staysphere.enums.AccountStatus;
import com.rajveersingh.staysphere.enums.ApprovalStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OwnerResponse {
    private UUID id;
    private String name;
    private String email;
    private String phoneNumber;
    private ApprovalStatus approvalStatus;
    private AccountStatus accountStatus;
    private LocalDateTime createdAt;
}
