package com.gatto.radar.recommendation;

import com.gatto.radar.demand.DemandSnapshot;
import com.gatto.radar.demand.DemandSnapshotRepository;
import com.gatto.radar.location.LocationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final DemandSnapshotRepository repository;
    private final ScoreCalculator calculator;
    private final LocationService locationService;

    public RecommendationService(
            DemandSnapshotRepository repository,
            ScoreCalculator calculator,
            LocationService locationService
    ) {
        this.repository = repository;
        this.calculator = calculator;
        this.locationService = locationService;
    }

    @Transactional(readOnly = true)
    public List<RadarItem> radar(
            double currentLat,
            double currentLon
    ) {
        Map<String, DemandSnapshot> latestByZone =
                repository.findTop100ByOrderByCapturedAtDesc()
                        .stream()
                        .collect(Collectors.toMap(
                                s -> s.getZone().getCode(),
                                s -> s,
                                (first, ignored) -> first,
                                LinkedHashMap::new
                        ));

        return latestByZone.values()
                .stream()
                .map(snapshot ->
                        toRadarItem(
                                snapshot,
                                currentLat,
                                currentLon
                        )
                )
                .sorted(
                        Comparator.comparingDouble(RadarItem::score)
                                .reversed()
                )
                .toList();
    }

    private RadarItem toRadarItem(
            DemandSnapshot snapshot,
            double currentLat,
            double currentLon
    ) {
        var zone = snapshot.getZone();

        double distanceKm = locationService.distanceKm(
                currentLat,
                currentLon,
                zone.getLatitude(),
                zone.getLongitude()
        );

        double relocationMinutes =
                locationService.estimateTravelMinutes(distanceKm);

        int travelMinutes =
                (int) Math.round(relocationMinutes);

        double supply =
                calculator.supplyFromEta(
                        snapshot.getPickupMinutes()
                );

        double score = calculator.score(
                snapshot.getDemand(),
                snapshot.getPickupMinutes(),
                snapshot.getAirportPressure(),
                snapshot.getFerryPressure(),
                snapshot.getEventPressure(),
                relocationMinutes
        );

        List<String> reasons = new ArrayList<>();

        if (snapshot.getDemand() >= 0.7) {
            reasons.add("high demand");
        }

        if (supply <= 0.5) {
            reasons.add("few nearby cars");
        }

        if (snapshot.getAirportPressure() >= 0.7) {
            reasons.add("airport pressure");
        }

        if (snapshot.getFerryPressure() >= 0.7) {
            reasons.add("ferry pressure");
        }

        if (snapshot.getEventPressure() >= 0.7) {
            reasons.add("event pressure");
        }

        if (travelMinutes <= 5) {
            reasons.add("nearby");
        }

        if (reasons.isEmpty()) {
            reasons.add("balanced conditions");
        }

        return new RadarItem(
                zone.getCode(),
                zone.getName(),
                snapshot.getCapturedAt(),

                score,

                Math.round(distanceKm * 10.0) / 10.0,
                travelMinutes,

                snapshot.getDemand(),
                snapshot.getPickupMinutes(),
                supply,

                reasons
        );
    }
}
