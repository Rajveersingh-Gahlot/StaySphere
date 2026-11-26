package com.rajveersingh.staysphere.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rajveersingh.staysphere.dto.property.PropertyImageResponse;
import com.rajveersingh.staysphere.service.PropertyImageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/owner/properties/{propertyId}/images")
@RequiredArgsConstructor
public class PropertyImageController {

    private final PropertyImageService propertyImageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PropertyImageResponse> uploadImage(
            @PathVariable UUID propertyId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID ownerId = getOwnerId(userDetails);

        PropertyImageResponse response = propertyImageService.uploadImage(propertyId, ownerId, file);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PropertyImageResponse>> getAllImages(
            @PathVariable UUID propertyId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        UUID ownerId = getOwnerId(userDetails);

        List<PropertyImageResponse> response = propertyImageService.getAlImages(propertyId,ownerId);

        return ResponseEntity.ok(response);
    }

    private UUID getOwnerId(UserDetails userDetails) {
        return UUID.fromString(userDetails.getUsername());
    }

}
