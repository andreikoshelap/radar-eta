package com.gatto.radar.eta;

import com.gatto.radar.zone.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface EtaReportRepository extends JpaRepository<EtaReport, UUID> {

    List<EtaReport> findByZoneAndSourceAndObservedAtAfter(
            Zone zone, EtaSource source, Instant after);

    // на будущее — если понадобится почистить старые сырые наблюдения
    void deleteByObservedAtBefore(Instant cutoff);
}