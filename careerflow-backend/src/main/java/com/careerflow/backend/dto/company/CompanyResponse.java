package com.careerflow.backend.dto.company;

import java.time.LocalDateTime;

public record CompanyResponse(
        Long id,
        String name,
        String website,
        String location,
        String industry,
        String logoUrl,
        long activeApplicationsCount,
        LocalDateTime createdAt
) {}
