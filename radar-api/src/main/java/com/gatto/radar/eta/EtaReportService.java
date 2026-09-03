package com.gatto.radar.eta;

import com.gatto.radar.zone.Zone;
import com.gatto.radar.zone.ZoneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class EtaReportService {

    private final EtaReportRepository reportRepository;
    private final ZoneRepository zoneRepository;

    public EtaReportService(EtaReportRepository reportRepository, ZoneRepository zoneRepository) {
        this.reportRepository = reportRepository;
        this.zoneRepository = zoneRepository;
    }

    @Transactional
    public EtaReport record(EtaReportRequest request, EtaSource source) {
        Zone zone = zoneRepository.findByCode(request.zoneCode())
                .orElseThrow(() -> new IllegalArgumentException("Unknown zone: " + request.zoneCode()));

        return reportRepository.save(new EtaReport(
                zone,
                request.observedPickupMinutes(),
                source,
                Instant.now()
        ));
    }
}