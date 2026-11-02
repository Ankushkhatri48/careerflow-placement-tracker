package com.careerflow.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Root controller to redirect browser visitors to Swagger UI documentation
 * or return API service metadata.
 */
@RestController
@Tag(name = "System", description = "Root health and status endpoint")
public class RootController {

    @GetMapping("/")
    @Operation(summary = "Redirect root requests to interactive Swagger UI documentation")
    public ResponseEntity<Void> redirectToSwagger() {
        return ResponseEntity.status(302).location(URI.create("/swagger-ui/index.html")).build();
    }

    @GetMapping("/api/v1/health")
    @Operation(summary = "Service health check")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> status = new HashMap<>();
        status.put("service", "CareerFlow Backend API");
        status.put("status", "UP");
        status.put("version", "1.0.0");
        return ResponseEntity.ok(status);
    }
}
