package com.example.weather.api.dto;

import com.example.weather.api.enums.MetricType;
import com.example.weather.api.enums.StatisticType;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Result of a statistic for one sensor and metric.
 */
public record MetricStatisticResponse(
        String sensorId,
        MetricType metric,
        StatisticType statistic,
        BigDecimal value,
        Instant from,
        Instant to
) { }
