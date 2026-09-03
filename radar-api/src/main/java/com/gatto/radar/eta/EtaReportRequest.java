package com.gatto.radar.eta;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record EtaReportRequest(
        @NotBlank String zoneCode,
        @Min(1) @Max(30) int observedPickupMinutes
) {}