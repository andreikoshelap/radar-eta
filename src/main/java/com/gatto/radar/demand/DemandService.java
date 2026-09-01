package com.gatto.radar.demand;

import com.gatto.radar.zone.Zone;
import com.gatto.radar.zone.ZoneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class DemandService {

    private final DemandSnapshotRepository snapshotRepository;
    private final ZoneRepository zoneRepository;

    public DemandService(DemandSnapshotRepository snapshotRepository, ZoneRepository zoneRepository) {
        this.snapshotRepository = snapshotRepository;
        this.zoneRepository = zoneRepository;
    }

    @Transactional
    public DemandSnapshot create(DemandRequest request) {
        Zone zone = zoneRepository.findByCode(request.zoneCode())
                .orElseThrow(() -> new IllegalArgumentException("Unknown zone: " + request.zoneCode()));

        return snapshotRepository.save(new DemandSnapshot(
                zone,
                Instant.now(),
                request.demand(),
                request.pickupMinutes(),
                request.airportPressure(),
                request.ferryPressure(),
                request.eventPressure()
        ));
    }
}
