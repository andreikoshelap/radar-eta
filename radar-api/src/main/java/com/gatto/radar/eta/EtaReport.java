package com.gatto.radar.eta;

import com.gatto.radar.zone.Zone;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "eta_report")
public class EtaReport {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone zone;

    @Column(nullable = false)
    private int observedPickupMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtaSource source; // CROWDSOURCED, SCRAPED

    @Column(nullable = false)
    private Instant observedAt;

    protected EtaReport() {}

    public EtaReport(Zone zone, int observedPickupMinutes, EtaSource source, Instant observedAt) {
        this.zone = zone;
        this.observedPickupMinutes = observedPickupMinutes;
        this.source = source;
        this.observedAt = observedAt;
    }

    public UUID getId() { return id; }
    public Zone getZone() { return zone; }
    public int getObservedPickupMinutes() { return observedPickupMinutes; }
    public EtaSource getSource() { return source; }
    public Instant getObservedAt() { return observedAt; }
}
