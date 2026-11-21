package com.rajveersingh.staysphere.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rajveersingh.staysphere.dto.property.CreatePropertyRequest;
import com.rajveersingh.staysphere.dto.property.PropertyResponse;
import com.rajveersingh.staysphere.service.PgPropertyService;

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

    private UUID getOwnerId(UserDetails userDetails) {
        return UUID.fromString(userDetails.getUsername());
    }

}
