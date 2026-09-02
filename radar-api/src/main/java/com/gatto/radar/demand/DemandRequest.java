package com.gatto.radar.demand;

import jakarta.validation.constraints.*;

public record DemandRequest(
        @NotBlank String zoneCode,
        @DecimalMin("0.0") @DecimalMax("1.0") double demand,
        @Min(1) @Max(30) int pickupMinutes,
        @DecimalMin("0.0") @DecimalMax("1.0") double airportPressure,
        @DecimalMin("0.0") @DecimalMax("1.0") double ferryPressure,
        @DecimalMin("0.0") @DecimalMax("1.0") double eventPressure
) {}
