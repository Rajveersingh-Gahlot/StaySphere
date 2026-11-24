package com.rajveersingh.staysphere.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rajveersingh.staysphere.dto.property.CreatePropertyRequest;
import com.rajveersingh.staysphere.dto.property.PropertyResponse;
import com.rajveersingh.staysphere.dto.property.UpdatePropertyRequest;
import com.rajveersingh.staysphere.dto.property.UpdatePropertyStatusRequest;
import com.rajveersingh.staysphere.service.PgPropertyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/owner/properties")
@RequiredArgsConstructor
public class PgPropertyController {

    private final PgPropertyService propertyService;

    @PostMapping
    public ResponseEntity<PropertyResponse> createProperty(
            @Valid @RequestBody CreatePropertyRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID ownerId = getOwnerId(userDetails);
        PropertyResponse response = propertyService.createProperty(request, ownerId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PropertyResponse>> getMyProperties(@AuthenticationPrincipal UserDetails userDetails) {

        UUID ownerId = getOwnerId(userDetails);

        List<PropertyResponse> properties = propertyService.getMyProperties(ownerId);

        return ResponseEntity.ok(properties);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponse> getPropertyById(@PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID ownerId = getOwnerId(userDetails);
        PropertyResponse property = propertyService.getPropertyById(id, ownerId);

        return ResponseEntity.ok(property);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyResponse> updateProperty(@PathVariable UUID id,
            @Valid @RequestBody UpdatePropertyRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID ownerId = getOwnerId(userDetails);

        PropertyResponse response = propertyService.updateProperty(id, ownerId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID ownerId = getOwnerId(userDetails);

        propertyService.deleteProperty(id, ownerId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PropertyResponse> updatePropertyStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePropertyStatusRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID ownerId = getOwnerId(userDetails);

        PropertyResponse response = propertyService.updatePropertyStatus(id, ownerId, request);

        return ResponseEntity.ok(response);
    }

    private UUID getOwnerId(UserDetails userDetails) {
        return UUID.fromString(userDetails.getUsername());
    }

}
