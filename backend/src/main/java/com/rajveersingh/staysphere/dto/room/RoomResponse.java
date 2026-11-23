
package com.rajveersingh.staysphere.dto.room;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomResponse {

    private UUID id;
    private UUID propertyId;

    private String roomNumber;
    private String description;
    private Integer floorNumber;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
