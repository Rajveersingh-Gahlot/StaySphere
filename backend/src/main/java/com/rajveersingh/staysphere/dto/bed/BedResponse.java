package com.rajveersingh.staysphere.dto.bed;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BedResponse {

    private UUID id;
    private UUID roomId;

    private String bedNumber;
    private BigDecimal monthlyRent;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
