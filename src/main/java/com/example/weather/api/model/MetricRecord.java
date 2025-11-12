package com.example.weather.api.model;

import com.example.weather.api.enums.MetricType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * One stored sensor reading.
 */
@Entity
@Table(name = "metric_records")
public class MetricRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sensor_id", nullable = false)
    private String sensorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "metric_type", nullable = false)
    private MetricType metricType;

    @Column(name = "metric_value", nullable = false, precision = 18, scale = 4)
    private BigDecimal value;

    @Column(name = "recorded_at", nullable = false)
    private Instant timestamp;

    protected MetricRecord() {
        // for JPA
    }

    public MetricRecord(String sensorId, MetricType metricType, BigDecimal value, Instant timestamp) {
        this.sensorId = sensorId;
        this.metricType = metricType;
        this.value = value;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public String getSensorId() {
        return sensorId;
    }

    public MetricType getMetricType() {
        return metricType;
    }

    public BigDecimal getValue() {
        return value;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
