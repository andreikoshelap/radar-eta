package com.gatto.radar.recommendation;

import java.time.Instant;
import java.util.List;

public record RadarItem(
        String zoneCode,
        String zoneName,
        Instant capturedAt,

        double score,

        double distanceKm,
        int travelMinutes,

        double demand,
        int pickupMinutes,
        double driverSupply,

        List<String> reasons
) {
}
