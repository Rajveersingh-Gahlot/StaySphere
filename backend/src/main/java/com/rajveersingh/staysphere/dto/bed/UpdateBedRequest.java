package com.rajveersingh.staysphere.dto.bed;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBedRequest {
    @NotBlank(message = "Bed number is required")
    @Size(max = 30)
    private String bedNumber;

    @NotNull(message = "Monthly rent is required")
    @DecimalMin(value = "1000", message = "Minimum monthly rent is ₹1000")
    @DecimalMax(value = "100000", message = "Maximum monthly rent is ₹100000")
    @Digits(integer = 6, fraction = 2)
    private BigDecimal monthlyRent;
}
