package com.careerflow.backend.dto.application;

import java.util.Map;

public record DashboardStatsResponse(
        long totalApplications,
        long activeInterviews,
        long offersReceived,
        long rejectedApplications,
        Map<String, Long> statusCounts
) {}
