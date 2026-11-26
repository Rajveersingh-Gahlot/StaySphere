package com.rajveersingh.staysphere.dto.property;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertyImageResponse {

    private UUID id;
    private UUID propertyId;
    private String imageUrl;
    private boolean isCover;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
