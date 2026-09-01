package com.gatto.radar.recommendation;

import com.gatto.radar.demand.DemandSnapshot;
import com.gatto.radar.demand.DemandSnapshotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final DemandSnapshotRepository repository;
    private final ScoreCalculator calculator;

    public RecommendationService(DemandSnapshotRepository repository, ScoreCalculator calculator) {
        this.repository = repository;
        this.calculator = calculator;
    }

    @Transactional(readOnly = true)
    public List<RadarItem> radar(double relocationMinutes) {
        Map<String, DemandSnapshot> latestByZone = repository.findTop100ByOrderByCapturedAtDesc()
                .stream()
                .collect(Collectors.toMap(
                        s -> s.getZone().getCode(),
                        s -> s,
                        (first, ignored) -> first,
                        LinkedHashMap::new
                ));

        return latestByZone.values().stream()
                .map(s -> toRadarItem(s, relocationMinutes))
                .sorted(Comparator.comparingDouble(RadarItem::score).reversed())
                .toList();
    }

    private RadarItem toRadarItem(DemandSnapshot s, double relocationMinutes) {
        double supply = calculator.supplyFromEta(s.getPickupMinutes());
        double score = calculator.score(
                s.getDemand(),
                s.getPickupMinutes(),
                s.getAirportPressure(),
                s.getFerryPressure(),
                s.getEventPressure(),
                relocationMinutes
        );

        List<String> reasons = new ArrayList<>();
        if (s.getDemand() >= 0.7) reasons.add("high demand");
        if (supply <= 0.5) reasons.add("few nearby cars");
        if (s.getAirportPressure() >= 0.7) reasons.add("airport pressure");
        if (s.getFerryPressure() >= 0.7) reasons.add("ferry pressure");
        if (s.getEventPressure() >= 0.7) reasons.add("event pressure");
        if (reasons.isEmpty()) reasons.add("balanced conditions");

        return new RadarItem(
                s.getZone().getCode(),
                s.getZone().getName(),
                s.getCapturedAt(),
                score,
                s.getDemand(),
                s.getPickupMinutes(),
                supply,
                reasons
        );
    }
}
