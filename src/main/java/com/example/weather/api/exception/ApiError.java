package com.example.weather.api.exception;

import java.time.Instant;

/**
 * Simple structure for error responses.
 */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) { }
