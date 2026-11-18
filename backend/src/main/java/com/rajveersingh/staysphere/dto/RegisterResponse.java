package com.rajveersingh.staysphere.dto;

import java.util.UUID;

import com.rajveersingh.staysphere.enums.ApprovalStatus;
import com.rajveersingh.staysphere.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor 
public class RegisterResponse {
    private UUID id;
    private String name;
    private String email;
    private String phoneNumber;
    private Role role;
    private ApprovalStatus approvalStatus;
    private String message;
}
