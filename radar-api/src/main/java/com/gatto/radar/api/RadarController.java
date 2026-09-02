package com.gatto.radar.api;

import com.gatto.radar.demand.DemandRequest;
import com.gatto.radar.demand.DemandService;
import com.gatto.radar.recommendation.RadarItem;
import com.gatto.radar.recommendation.RecommendationService;
import com.gatto.radar.zone.Zone;
import com.gatto.radar.zone.ZoneRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
public class RadarController {

    private final ZoneRepository zoneRepository;
    private final DemandService demandService;
    private final RecommendationService recommendationService;

    public RadarController(
            ZoneRepository zoneRepository,
            DemandService demandService,
            RecommendationService recommendationService
    ) {
        this.zoneRepository = zoneRepository;
        this.demandService = demandService;
        this.recommendationService = recommendationService;
    }

    @GetMapping("/zones")
    public List<Zone> zones() {
        return zoneRepository.findAll();
    }

    @PostMapping("/demand")
    @ResponseStatus(HttpStatus.CREATED)
    public void demand(@Valid @RequestBody DemandRequest request) {
        demandService.create(request);
    }

    @GetMapping("/radar")
    public List<RadarItem> radar(
            @RequestParam
            @DecimalMin("-90.0")
            @DecimalMax("90.0")
            double lat,

            @RequestParam
            @DecimalMin("-180.0")
            @DecimalMax("180.0")
            double lon
    ) {
        return recommendationService.radar(lat, lon);
    }
}
