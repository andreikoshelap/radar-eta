package com.gatto.radar.demand;

import com.gatto.radar.zone.Zone;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "demand_snapshots")
public class DemandSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Zone zone;

    @Column(nullable = false)
    private Instant capturedAt;

    @Column(nullable = false)
    private double demand;

    @Column(nullable = false)
    private int pickupMinutes;

    @Column(nullable = false)
    private double airportPressure;

    @Column(nullable = false)
    private double ferryPressure;

    @Column(nullable = false)
    private double eventPressure;

    protected DemandSnapshot() {}

    public DemandSnapshot(
            Zone zone,
            Instant capturedAt,
            double demand,
            int pickupMinutes,
            double airportPressure,
            double ferryPressure,
            double eventPressure
    ) {
        this.zone = zone;
        this.capturedAt = capturedAt;
        this.demand = demand;
        this.pickupMinutes = pickupMinutes;
        this.airportPressure = airportPressure;
        this.ferryPressure = ferryPressure;
        this.eventPressure = eventPressure;
    }

    public Long getId() { return id; }
    public Zone getZone() { return zone; }
    public Instant getCapturedAt() { return capturedAt; }
    public double getDemand() { return demand; }
    public int getPickupMinutes() { return pickupMinutes; }
    public double getAirportPressure() { return airportPressure; }
    public double getFerryPressure() { return ferryPressure; }
    public double getEventPressure() { return eventPressure; }
}
