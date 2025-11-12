package com.example.weather.api.dto;

import com.example.weather.api.enums.MetricType;
import com.example.weather.api.enums.StatisticType;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

/**
 * Filters for querying statistics.
 */
public record MetricQueryRequest(
        List<String> sensorIds,
        List<MetricType> metrics,
        @NotNull StatisticType statistic,
        Instant from,
        Instant to
) { }
