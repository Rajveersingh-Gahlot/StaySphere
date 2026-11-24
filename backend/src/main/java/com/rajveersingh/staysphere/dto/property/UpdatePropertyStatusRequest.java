package com.rajveersingh.staysphere.dto.property;

import com.rajveersingh.staysphere.enums.property.PropertyStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePropertyStatusRequest {

    @NotNull(message = "Property status is required")
    private PropertyStatus status;
}