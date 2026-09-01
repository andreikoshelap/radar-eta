package com.gatto.radar.demand;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DemandSnapshotRepository extends JpaRepository<DemandSnapshot, Long> {
    List<DemandSnapshot> findTop100ByOrderByCapturedAtDesc();
}
