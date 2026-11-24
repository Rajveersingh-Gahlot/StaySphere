package com.rajveersingh.staysphere.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rajveersingh.staysphere.dto.bed.BedResponse;
import com.rajveersingh.staysphere.dto.bed.CreateBedRequest;
import com.rajveersingh.staysphere.service.BedService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/owner/properties/{propertyId}/rooms/{roomId}/beds")
public class BedController {

    private final BedService bedService;

    @PostMapping
    public ResponseEntity<BedResponse> createBed(@PathVariable UUID propertyId,
            @PathVariable UUID roomId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateBedRequest request) {

        UUID ownerId = getOwnerId(userDetails);

        BedResponse response = bedService.createBed(roomId, propertyId, ownerId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<BedResponse>> getAllBeds(@PathVariable UUID propertyId,
            @PathVariable UUID roomId,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID ownerId = getOwnerId(userDetails);

        List<BedResponse> response = bedService.getAllBeds(propertyId, roomId, ownerId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bedId}")
    public ResponseEntity<BedResponse>getBedById(@PathVariable  UUID bedId,
            @PathVariable UUID propertyId,
            @PathVariable UUID roomId,
            @AuthenticationPrincipal UserDetails userDetails){

                UUID ownerId = getOwnerId(userDetails);

                BedResponse response = bedService.getBedById(propertyId, ownerId, roomId, bedId);

                return ResponseEntity.ok(response);
            }

    private UUID getOwnerId(UserDetails userDetails) {
        return UUID.fromString(userDetails.getUsername());
    }
}
