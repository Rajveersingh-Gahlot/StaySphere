package com.rajveersingh.staysphere.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rajveersingh.staysphere.dto.PendingOwnerResponse;
import com.rajveersingh.staysphere.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService service;

    @GetMapping("/owners/pending")
    public ResponseEntity<List<PendingOwnerResponse>> getPendingOwners() {
        List<PendingOwnerResponse> response = service.getPendingOwners();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
