package com.careerflow.backend.dto.interview;

import com.careerflow.backend.entity.RoundStatus;
import com.careerflow.backend.entity.RoundType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record InterviewRoundRequest(
        @NotBlank(message = "Round title is required")
        @Size(min = 2, max = 120, message = "Round title must be between 2 and 120 characters")
        String roundTitle,

        @NotNull(message = "Round type is required")
        RoundType roundType,

        RoundStatus status,

        @NotNull(message = "Scheduled time is required")
        LocalDateTime scheduledTime,

        String meetingLink,
        String feedback
) {}
