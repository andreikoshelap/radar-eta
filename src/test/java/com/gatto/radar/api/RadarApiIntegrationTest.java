package com.gatto.radar.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        properties = {
                "spring.datasource.url=jdbc:h2:mem:radar-api-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
        }
)
@AutoConfigureMockMvc
class RadarApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void radarReturnsLatestDemandSnapshot() throws Exception {
        mockMvc.perform(post("/api/demand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "zoneCode": "ULEMISTE",
                                  "demand": 0.8,
                                  "pickupMinutes": 6,
                                  "airportPressure": 0.6,
                                  "ferryPressure": 0.0,
                                  "eventPressure": 0.0
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/radar")
                        .param("lat", "59.42130")
                        .param("lon", "24.79380"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].zoneCode").value("ULEMISTE"));
    }
}
