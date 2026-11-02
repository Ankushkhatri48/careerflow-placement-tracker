package com.careerflow.backend.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error representation for client-side API consumers.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        Map<String, String> validationErrors,
        LocalDateTime timestamp
) {
    public ErrorResponse(int status, String error, String message, String path) {
        this(status, error, message, path, null, LocalDateTime.now());
    }

    public ErrorResponse(int status, String error, String message, String path, Map<String, String> validationErrors) {
        this(status, error, message, path, validationErrors, LocalDateTime.now());
    }
}
