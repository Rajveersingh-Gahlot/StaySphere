
package com.rajveersingh.staysphere.dto.property;

import com.rajveersingh.staysphere.enums.property.PgType;
import com.rajveersingh.staysphere.enums.property.PropertyStatus;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertyResponse {

    private UUID id;
    private UUID ownerId;

    private String name;
    private String description;
    private String address;
    private String city;
    private String state;
    private String pincode;

    private PgType pgType;
    private PropertyStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
