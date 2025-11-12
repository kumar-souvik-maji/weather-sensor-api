package com.example.weather.api.controller;

import com.example.weather.api.enums.MetricType;
import com.example.weather.api.enums.StatisticType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Basic integration test for controller endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
class MetricsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper json;

    @Test
    void shouldIngestAndQueryHumiditySuccessfully() throws Exception {
        var ingestBody = Map.of(
                "sensorId", "S1",
                "metric", MetricType.HUMIDITY.name(),
                "value", new BigDecimal("55.5"),
                "timestamp", Instant.now().toString()
        );

        mockMvc.perform(post("/api/metrics/ingest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(ingestBody)))
                .andExpect(status().isCreated());

        var queryBody = Map.of(
                "sensorIds", List.of("S1"),
                "metrics", List.of(MetricType.HUMIDITY.name()),
                "statistic", StatisticType.AVG.name()
        );

        mockMvc.perform(post("/api/metrics/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(queryBody)))
                .andExpect(status().isOk());
    }
}
