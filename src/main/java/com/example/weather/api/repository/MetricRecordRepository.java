package com.example.weather.api.repository;

import com.example.weather.api.model.MetricRecord;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Simple repository for metric records.
 *
 * We intentionally keep this interface small and perform
 * filtering in the service layer so the flow is easier to follow.
 */
public interface MetricRecordRepository extends JpaRepository<MetricRecord, Long> {
    // JpaRepository already provides CRUD methods like:
    // findAll(), findById(), save(), deleteById(), etc.
}
