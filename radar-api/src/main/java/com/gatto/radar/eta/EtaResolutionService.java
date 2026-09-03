package com.gatto.radar.eta;

import com.gatto.radar.zone.Zone;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class EtaResolutionService {

    private static final Duration FRESHNESS_WINDOW = Duration.ofMinutes(10);
    private static final int MIN_REPORTS_FOR_TRUST = 2; // одному репорту не верим

    private final EtaReportRepository reportRepository;
    private final BoltEtaScraperService scraperService; // fallback, из прошлого разговора

    public EtaResolutionService(EtaReportRepository reportRepository, BoltEtaScraperService scraperService) {
        this.reportRepository = reportRepository;
        this.scraperService = scraperService;
    }

    int resolvePickupMinutes(Zone zone) {
        List<EtaReport> recent = reportRepository
                .findByZoneAndSourceAndObservedAtAfter(
                        zone, EtaSource.CROWDSOURCED, Instant.now().minus(FRESHNESS_WINDOW));

        if (recent.size() >= MIN_REPORTS_FOR_TRUST) {
            return (int) Math.round(recent.stream()
                    .mapToInt(EtaReport::getObservedPickupMinutes)
                    .average().orElseThrow());
        }

        // недостаточно органических данных — fallback на скрейпинг
        return scraperService.fetchEtaForZone(zone)
                .map(eta -> {
                    reportRepository.save(new EtaReport(zone, (int) eta.toMinutes(),
                            EtaSource.SCRAPED, Instant.now())); // логируем и это наблюдение тоже
                    return (int) eta.toMinutes();
                })
                .orElse(15); // консервативный дефолт, если вообще ничего нет
    }
}