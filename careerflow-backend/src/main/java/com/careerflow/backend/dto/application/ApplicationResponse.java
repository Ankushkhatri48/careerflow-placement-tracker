package com.careerflow.backend.dto.application;

import com.careerflow.backend.dto.company.CompanyResponse;
import com.careerflow.backend.entity.ApplicationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public record ApplicationResponse(
        Long id,
        CompanyResponse company,
        String jobTitle,
        String jobUrl,
        String jobLocation,
        BigDecimal salaryMin,
        BigDecimal salaryMax,
        ApplicationStatus status,
        LocalDate appliedDate,
        String notes,
        Set<String> skills,
        int interviewRoundsCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
