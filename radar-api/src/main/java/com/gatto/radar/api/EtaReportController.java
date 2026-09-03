package com.gatto.radar.api;

import com.gatto.radar.eta.EtaReportRequest;
import com.gatto.radar.eta.EtaReportService;
import com.gatto.radar.eta.EtaSource;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/eta-report")
public class EtaReportController {

    private final EtaReportService etaReportService;

    public EtaReportController(EtaReportService etaReportService) {
        this.etaReportService = etaReportService;
    }

    @PostMapping
    public ResponseEntity<Void> report(@RequestBody @Valid EtaReportRequest request) {
        etaReportService.record(request, EtaSource.CROWDSOURCED);
        return ResponseEntity.accepted().build();
    }
}