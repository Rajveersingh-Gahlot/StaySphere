package com.rajveersingh.staysphere.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.rajveersingh.staysphere.enums.ApprovalStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PendingOwnerResponse {
    private UUID id;
    private String name;
    private String email;
    private String phoneNumber;
    private ApprovalStatus approvalStatus;
    private LocalDateTime createdAt;
}
