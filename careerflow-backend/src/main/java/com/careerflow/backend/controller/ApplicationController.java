package com.careerflow.backend.controller;

import com.careerflow.backend.dto.application.ApplicationRequest;
import com.careerflow.backend.dto.application.ApplicationResponse;
import com.careerflow.backend.dto.application.DashboardStatsResponse;
import com.careerflow.backend.dto.application.StatusUpdateRequest;
import com.careerflow.backend.dto.common.ApiResponse;
import com.careerflow.backend.dto.common.PageResponse;
import com.careerflow.backend.entity.ApplicationStatus;
import com.careerflow.backend.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/applications")
@Tag(name = "Applications", description = "Endpoints for managing job applications, status transitions, dynamic filtering, and analytics")
@SecurityRequirement(name = "bearerAuth")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    @Operation(summary = "Create a new job application")
    public ResponseEntity<ApiResponse<ApplicationResponse>> createApplication(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ApplicationRequest request
    ) {
        ApplicationResponse created = applicationService.createApplication(userDetails.getUsername(), request);
        return new ResponseEntity<>(ApiResponse.success("Job application created successfully", created), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get application details by ID")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getApplicationById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id
    ) {
        ApplicationResponse app = applicationService.getApplicationById(userDetails.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.success(app));
    }

    @GetMapping
    @Operation(summary = "Search, filter, paginate and sort job applications")
    public ResponseEntity<ApiResponse<PageResponse<ApplicationResponse>>> getApplications(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "appliedDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PageResponse<ApplicationResponse> response = applicationService.getApplications(
                userDetails.getUsername(),
                search,
                status,
                companyId,
                startDate,
                endDate,
                pageable
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing job application")
    public ResponseEntity<ApiResponse<ApplicationResponse>> updateApplication(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody ApplicationRequest request
    ) {
        ApplicationResponse updated = applicationService.updateApplication(userDetails.getUsername(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Application updated successfully", updated));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Quick update application status")
    public ResponseEntity<ApiResponse<ApplicationResponse>> updateStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request
    ) {
        ApplicationResponse updated = applicationService.updateStatus(userDetails.getUsername(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Application status updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an application")
    public ResponseEntity<ApiResponse<Void>> deleteApplication(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id
    ) {
        applicationService.deleteApplication(userDetails.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.success("Application deleted successfully", null));
    }

    @GetMapping("/dashboard/stats")
    @Operation(summary = "Get aggregated analytics metrics for the dashboard")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        DashboardStatsResponse stats = applicationService.getDashboardStats(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
