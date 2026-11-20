package com.rajveersingh.staysphere.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rajveersingh.staysphere.dto.OwnerResponse;
import com.rajveersingh.staysphere.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService service;

    @GetMapping("/owners/pending")
    public ResponseEntity<List<OwnerResponse>> getPendingOwners() {
        List<OwnerResponse> response = service.getPendingOwners();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/owners/{id}/approve")
    public ResponseEntity<Void> approveOwner(@PathVariable UUID id) {
        service.approveOwner(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/owners/{id}/reject")
    public ResponseEntity<Void> rejectOwner(@PathVariable UUID id) {

        service.rejectOwner(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/owners")
    public ResponseEntity<List<OwnerResponse>> getallOwners() {
        List<OwnerResponse> response = service.getAllOwners();
        return ResponseEntity.ok(response);
    }

}
