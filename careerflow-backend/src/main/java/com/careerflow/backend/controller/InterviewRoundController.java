package com.careerflow.backend.controller;

import com.careerflow.backend.dto.common.ApiResponse;
import com.careerflow.backend.dto.interview.InterviewRoundRequest;
import com.careerflow.backend.dto.interview.InterviewRoundResponse;
import com.careerflow.backend.service.InterviewRoundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/applications/{applicationId}/interviews")
@Tag(name = "Interviews", description = "Endpoints for scheduling, updating, and tracking interview rounds per job application")
@SecurityRequirement(name = "bearerAuth")
public class InterviewRoundController {

    private final InterviewRoundService interviewRoundService;

    public InterviewRoundController(InterviewRoundService interviewRoundService) {
        this.interviewRoundService = interviewRoundService;
    }

    @PostMapping
    @Operation(summary = "Schedule a new interview round for an application")
    public ResponseEntity<ApiResponse<InterviewRoundResponse>> addRound(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long applicationId,
            @Valid @RequestBody InterviewRoundRequest request
    ) {
        InterviewRoundResponse round = interviewRoundService.addRound(userDetails.getUsername(), applicationId, request);
        return new ResponseEntity<>(ApiResponse.success("Interview round scheduled successfully", round), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all interview rounds for an application")
    public ResponseEntity<ApiResponse<List<InterviewRoundResponse>>> getRounds(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long applicationId
    ) {
        List<InterviewRoundResponse> rounds = interviewRoundService.getRoundsForApplication(userDetails.getUsername(), applicationId);
        return ResponseEntity.ok(ApiResponse.success(rounds));
    }

    @PutMapping("/{roundId}")
    @Operation(summary = "Update an existing interview round")
    public ResponseEntity<ApiResponse<InterviewRoundResponse>> updateRound(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long applicationId,
            @PathVariable Long roundId,
            @Valid @RequestBody InterviewRoundRequest request
    ) {
        InterviewRoundResponse round = interviewRoundService.updateRound(userDetails.getUsername(), applicationId, roundId, request);
        return ResponseEntity.ok(ApiResponse.success("Interview round updated successfully", round));
    }

    @DeleteMapping("/{roundId}")
    @Operation(summary = "Delete an interview round")
    public ResponseEntity<ApiResponse<Void>> deleteRound(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long applicationId,
            @PathVariable Long roundId
    ) {
        interviewRoundService.deleteRound(userDetails.getUsername(), applicationId, roundId);
        return ResponseEntity.ok(ApiResponse.success("Interview round deleted successfully", null));
    }
}
