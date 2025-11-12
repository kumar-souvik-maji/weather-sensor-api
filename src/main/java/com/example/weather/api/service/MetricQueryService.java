package com.example.weather.api.service;

import com.example.weather.api.dto.MetricQueryRequest;
import com.example.weather.api.dto.MetricStatisticResponse;
import com.example.weather.api.enums.MetricType;
import com.example.weather.api.enums.StatisticType;
import com.example.weather.api.model.MetricRecord;
import com.example.weather.api.repository.MetricRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles querying metrics and computing statistics.
 * - repository just returns all records
 * - all filtering happens step-by-step in Java
 */
@Service
public class MetricQueryService {

    private final MetricRecordRepository repository;

    public MetricQueryService(MetricRecordRepository repository) {
        this.repository = repository;
    }

    /**
     * Returns statistics for the selected sensors and metrics.
     *
     * <p>If a date range is provided, it must be between one day and one month.
     * When no range is provided, the latest data for each sensor + metric is used.</p>
     */
    @Transactional(readOnly = true)
    public List<MetricStatisticResponse> query(MetricQueryRequest request) {
        validateRange(request);

        // Load all records. For a coding challenge this is fine and
        // keeps the code easy to understand.
        List<MetricRecord> allRecords = repository.findAll();

        // Step 1: filter by sensorIds and metrics if they are provided.
        List<MetricRecord> filtered = allRecords.stream()
                .filter(r -> request.sensorIds() == null
                        || request.sensorIds().isEmpty()
                        || request.sensorIds().contains(r.getSensorId()))
                .filter(r -> request.metrics() == null
                        || request.metrics().isEmpty()
                        || request.metrics().contains(r.getMetricType()))
                .toList();

        Instant from = request.from();
        Instant to = request.to();
        boolean hasRange = from != null && to != null;

        // Step 2: optional filter by time range.
        if (hasRange) {
            filtered = filtered.stream()
                    .filter(r -> !r.getTimestamp().isBefore(from))
                    .filter(r -> !r.getTimestamp().isAfter(to))
                    .toList();
        }

        if (filtered.isEmpty()) {
            return List.of();
        }

        // Step 3: group by sensor + metric.
        Map<Key, List<MetricRecord>> grouped = filtered.stream()
                .collect(Collectors.groupingBy(r -> new Key(r.getSensorId(), r.getMetricType())));

        // Step 4: for each group, compute the requested statistic.
        return grouped.entrySet().stream()
                .map(entry -> {
                    String sensorId = entry.getKey().sensorId();
                    MetricType metricType = entry.getKey().metric();
                    List<MetricRecord> groupRecords = entry.getValue();

                    List<MetricRecord> effectiveRecords;
                    Instant groupFrom;
                    Instant groupTo;

                    if (hasRange) {
                        // When range is provided, use all records inside that range.
                        effectiveRecords = groupRecords;
                        groupFrom = from;
                        groupTo = to;
                    } else {
                        // When no range is provided, use only the latest record(s)
                        // per sensor + metric.
                        Instant latest = groupRecords.stream()
                                .map(MetricRecord::getTimestamp)
                                .max(Instant::compareTo)
                                .orElseThrow();

                        effectiveRecords = groupRecords.stream()
                                .filter(r -> r.getTimestamp().equals(latest))
                                .toList();

                        groupFrom = latest;
                        groupTo = latest;
                    }

                    BigDecimal value = computeStatistic(effectiveRecords, request.statistic());

                    return new MetricStatisticResponse(
                            sensorId,
                            metricType,
                            request.statistic(),
                            value,
                            groupFrom,
                            groupTo
                    );
                })
                .toList();
    }

    /**
     * Ensures the date range is either absent or between one day and one month.
     */
    private void validateRange(MetricQueryRequest request) {
        Instant from = request.from();
        Instant to = request.to();

        if (from == null && to == null) {
            // latest data mode, no validation needed
            return;
        }
        if (from == null || to == null) {
            throw new IllegalArgumentException("Both 'from' and 'to' must be provided, or neither.");
        }
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException("'from' must be before 'to'.");
        }

        Duration duration = Duration.between(from, to);
        Duration min = Duration.ofDays(1);
        Duration max = Duration.ofDays(31);

        if (duration.compareTo(min) < 0 || duration.compareTo(max) > 0) {
            throw new IllegalArgumentException("Date range must be between one day and one month.");
        }
    }

    /**
     * Computes a statistic for the given records.
     * (This can later be refactored into a Strategy pattern if needed.)
     */
    private BigDecimal computeStatistic(List<MetricRecord> records, StatisticType type) {
        switch (type) {
            case MIN:
                return records.stream()
                        .map(MetricRecord::getValue)
                        .min(Comparator.naturalOrder())
                        .orElse(BigDecimal.ZERO)
                        .setScale(4, RoundingMode.HALF_UP);
            case MAX:
                return records.stream()
                        .map(MetricRecord::getValue)
                        .max(Comparator.naturalOrder())
                        .orElse(BigDecimal.ZERO)
                        .setScale(4, RoundingMode.HALF_UP);
            case SUM: {
                BigDecimal sum = records.stream()
                        .map(MetricRecord::getValue)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                return sum.setScale(4, RoundingMode.HALF_UP);
            }
            case AVG:
            default: {
                if (records.isEmpty()) {
                    return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
                }
                BigDecimal sum = records.stream()
                        .map(MetricRecord::getValue)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                return sum.divide(BigDecimal.valueOf(records.size()), 4, RoundingMode.HALF_UP);
            }
        }
    }

    /**
     * Small helper key used for grouping.
     */
    private record Key(String sensorId, MetricType metric) { }
}
