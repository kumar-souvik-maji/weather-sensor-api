package com.example.weather.api.service;

import com.example.weather.api.dto.MetricIngestionRequest;
import com.example.weather.api.model.MetricRecord;
import com.example.weather.api.repository.MetricRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles saving new sensor readings.
 */
@Service
public class MetricIngestionService {

    private final MetricRecordRepository repository;

    public MetricIngestionService(MetricRecordRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void ingest(MetricIngestionRequest request) {
        MetricRecord record = new MetricRecord(
                request.sensorId(),
                request.metric(),
                request.value(),
                request.timestamp()
        );
        repository.save(record);
    }
}
