CREATE TABLE eta_report (
                            id UUID PRIMARY KEY,
                            zone_id BIGINT NOT NULL REFERENCES zones(id),
                            observed_pickup_minutes INT NOT NULL,
                            source VARCHAR(20) NOT NULL,
                            observed_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX idx_eta_report_zone_time ON eta_report(zone_id, observed_at DESC);
