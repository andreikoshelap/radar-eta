package com.gatto.radar.recommendation;

import org.springframework.stereotype.Component;

@Component
public class ScoreCalculator {

    public double score(
            double demand,
            int pickupMinutes,
            double airportPressure,
            double ferryPressure,
            double eventPressure,
            double relocationMinutes
    ) {
        double driverSupply = supplyFromEta(pickupMinutes);

        double raw =
                  40 * demand
                + 30 * (1 - driverSupply)
                + 10 * airportPressure
                + 10 * ferryPressure
                + 10 * eventPressure
                - 1.5 * relocationMinutes;

        return Math.max(0, Math.min(100, Math.round(raw * 10.0) / 10.0));
    }

    public double supplyFromEta(int minutes) {
        if (minutes <= 2) return 1.0;
        if (minutes == 3) return 0.8;
        if (minutes == 4) return 0.65;
        if (minutes == 5) return 0.5;
        if (minutes == 6) return 0.35;
        return 0.2;
    }
}
