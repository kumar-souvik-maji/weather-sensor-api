package com.example.weather.api.controller;

import com.example.weather.api.dto.MetricIngestionRequest;
import com.example.weather.api.dto.MetricQueryRequest;
import com.example.weather.api.dto.MetricStatisticResponse;
import com.example.weather.api.service.MetricIngestionService;
import com.example.weather.api.service.MetricQueryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints for ingesting and querying metrics.
 */
@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    private final MetricIngestionService ingestionService;
    private final MetricQueryService queryService;

    public MetricsController(MetricIngestionService ingestionService,
                             MetricQueryService queryService) {
        this.ingestionService = ingestionService;
        this.queryService = queryService;
    }

    /**
     * Ingests a new sensor reading.
     */
    @PostMapping("/ingest")
    public ResponseEntity<Void> ingest(@RequestBody @Valid MetricIngestionRequest request) {
        ingestionService.ingest(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Returns statistics for selected sensors and metrics.
     */
    @PostMapping("/query")
    public ResponseEntity<List<MetricStatisticResponse>> query(
            @RequestBody @Valid MetricQueryRequest request) {
        List<MetricStatisticResponse> responses = queryService.query(request);
        return ResponseEntity.ok(responses);
    }
}
