package com.rajveersingh.staysphere.dto;

import java.util.UUID;

import com.rajveersingh.staysphere.enums.AccountStatus;
import com.rajveersingh.staysphere.enums.ApprovalStatus;
import com.rajveersingh.staysphere.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private UUID id;
    private String name;
    private String email;
    private Role role;
    private ApprovalStatus approvalStatus;
    private AccountStatus accountStatus;
    private String message;
}
