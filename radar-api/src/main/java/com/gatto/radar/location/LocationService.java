package com.gatto.radar.location;

import org.springframework.stereotype.Service;

@Service
public class LocationService {

    private static final double EARTH_RADIUS_KM = 6371.0;

    // Для первого MVP.
    // Позже заменим на routing API.
    private static final double AVERAGE_CITY_SPEED_KMH = 28.0;

    public double distanceKm(
            double fromLat,
            double fromLon,
            double toLat,
            double toLon
    ) {
        double lat1 = Math.toRadians(fromLat);
        double lat2 = Math.toRadians(toLat);

        double deltaLat = Math.toRadians(toLat - fromLat);
        double deltaLon = Math.toRadians(toLon - fromLon);

        double a =
                Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                        + Math.cos(lat1)
                        * Math.cos(lat2)
                        * Math.sin(deltaLon / 2)
                        * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(
                Math.sqrt(a),
                Math.sqrt(1 - a)
        );

        return EARTH_RADIUS_KM * c;
    }

    public double estimateTravelMinutes(double distanceKm) {
        double hours = distanceKm / AVERAGE_CITY_SPEED_KMH;
        return hours * 60;
    }
}