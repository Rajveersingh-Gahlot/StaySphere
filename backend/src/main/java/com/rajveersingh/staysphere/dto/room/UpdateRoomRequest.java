
package com.rajveersingh.staysphere.dto.room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRoomRequest {

    @NotBlank(message = "Room number is required")
    @Size(max = 30, message = "Room number must not exceed 30 characters")
    private String roomNumber;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    @NotNull(message = "Floor number is required")
    private Integer floorNumber;
}
