package com.careerflow.backend.dto.application;

import com.careerflow.backend.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(
        @NotNull(message = "Status cannot be null")
        ApplicationStatus status
) {}
