package com.example.weather.api.dto;

import com.example.weather.api.enums.MetricType;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Request body for sending a new sensor reading.
 */
public record MetricIngestionRequest(
        @NotBlank String sensorId,
        @NotNull MetricType metric,
        @NotNull @Digits(integer = 14, fraction = 4) BigDecimal value,
        @NotNull Instant timestamp
) { }
