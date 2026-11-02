package com.careerflow.backend.dto.application;

import com.careerflow.backend.entity.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record ApplicationRequest(
        @NotNull(message = "Company ID is required")
        Long companyId,

        @NotBlank(message = "Job title is required")
        @Size(min = 2, max = 150, message = "Job title must be between 2 and 150 characters")
        String jobTitle,

        String jobUrl,
        String jobLocation,

        @PositiveOrZero(message = "Minimum salary cannot be negative")
        BigDecimal salaryMin,

        @PositiveOrZero(message = "Maximum salary cannot be negative")
        BigDecimal salaryMax,

        ApplicationStatus status,
        LocalDate appliedDate,
        String notes,
        Set<String> skills
) {}
