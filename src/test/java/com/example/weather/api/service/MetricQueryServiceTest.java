package com.example.weather.api.service;

import com.example.weather.api.dto.MetricIngestionRequest;
import com.example.weather.api.dto.MetricQueryRequest;
import com.example.weather.api.dto.MetricStatisticResponse;
import com.example.weather.api.enums.MetricType;
import com.example.weather.api.enums.StatisticType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Service-level tests for MetricQueryService.
 * Covers all MetricType values and different statistic modes.
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MetricQueryServiceTest {

    @Autowired
    private MetricIngestionService ingestionService;

    @Autowired
    private MetricQueryService queryService;

    /**
     *  Test AVG statistic for TEMPERATURE within valid date range (1 day)
     */
    @Test
    void averageTemperatureForSensorInRange_shouldWork() {
        Instant now = Instant.now();

        ingestionService.ingest(new MetricIngestionRequest(
                "sensor-1", MetricType.TEMPERATURE,
                new BigDecimal("20.0"), now.minusSeconds(3600)));

        ingestionService.ingest(new MetricIngestionRequest(
                "sensor-1", MetricType.TEMPERATURE,
                new BigDecimal("22.0"), now.minusSeconds(1800)));

        ingestionService.ingest(new MetricIngestionRequest(
                "sensor-1", MetricType.TEMPERATURE,
                new BigDecimal("24.0"), now.minusSeconds(600)));

        MetricQueryRequest request = new MetricQueryRequest(
                List.of("sensor-1"),
                List.of(MetricType.TEMPERATURE),
                StatisticType.AVG,
                now.minusSeconds(24 * 3600), // 1 day range
                now
        );

        List<MetricStatisticResponse> responses = queryService.query(request);

        assertThat(responses).hasSize(1);
        MetricStatisticResponse response = responses.get(0);

        assertThat(response.sensorId()).isEqualTo("sensor-1");
        assertThat(response.metric()).isEqualTo(MetricType.TEMPERATURE);
        assertThat(response.statistic()).isEqualTo(StatisticType.AVG);
        assertThat(response.value()).isEqualByComparingTo("22.0000");
    }

    /**
     *  Test MAX statistic for WIND_SPEED metric within valid date range
     */
    @Test
    void maxWindSpeedForSensorInRange_shouldWork() {
        Instant now = Instant.now();

        ingestionService.ingest(new MetricIngestionRequest(
                "sensor-2", MetricType.WIND_SPEED,
                new BigDecimal("12.5"), now.minusSeconds(7200))); // 2h ago

        ingestionService.ingest(new MetricIngestionRequest(
                "sensor-2", MetricType.WIND_SPEED,
                new BigDecimal("15.7"), now.minusSeconds(3600))); // 1h ago

        ingestionService.ingest(new MetricIngestionRequest(
                "sensor-2", MetricType.WIND_SPEED,
                new BigDecimal("13.2"), now.minusSeconds(1800))); // 30m ago

        MetricQueryRequest request = new MetricQueryRequest(
                List.of("sensor-2"),
                List.of(MetricType.WIND_SPEED),
                StatisticType.MAX,
                now.minusSeconds(24 * 3600),
                now
        );

        List<MetricStatisticResponse> responses = queryService.query(request);

        assertThat(responses).hasSize(1);
        MetricStatisticResponse response = responses.get(0);

        assertThat(response.sensorId()).isEqualTo("sensor-2");
        assertThat(response.metric()).isEqualTo(MetricType.WIND_SPEED);
        assertThat(response.statistic()).isEqualTo(StatisticType.MAX);
        assertThat(response.value()).isEqualByComparingTo("15.7000");
    }

    /**
     *  Test latest-data mode (no date range) for HUMIDITY using AVG statistic
     */
    @Test
    void latestHumidityWithoutRange_shouldUseLatestRecord() {
        Instant now = Instant.now();

        ingestionService.ingest(new MetricIngestionRequest(
                "sensor-3", MetricType.HUMIDITY,
                new BigDecimal("40.0"), now.minusSeconds(7200)));

        ingestionService.ingest(new MetricIngestionRequest(
                "sensor-3", MetricType.HUMIDITY,
                new BigDecimal("55.0"), now.minusSeconds(3600)));

        ingestionService.ingest(new MetricIngestionRequest(
                "sensor-3", MetricType.HUMIDITY,
                new BigDecimal("60.0"), now)); // latest

        MetricQueryRequest request = new MetricQueryRequest(
                List.of("sensor-3"),
                List.of(MetricType.HUMIDITY),
                StatisticType.AVG,
                null,  // no range -> latest data mode
                null
        );

        List<MetricStatisticResponse> responses = queryService.query(request);

        assertThat(responses).hasSize(1);
        MetricStatisticResponse response = responses.get(0);

        assertThat(response.sensorId()).isEqualTo("sensor-3");
        assertThat(response.metric()).isEqualTo(MetricType.HUMIDITY);
        assertThat(response.statistic()).isEqualTo(StatisticType.AVG);
        assertThat(response.value()).isEqualByComparingTo("60.0000");
    }
}