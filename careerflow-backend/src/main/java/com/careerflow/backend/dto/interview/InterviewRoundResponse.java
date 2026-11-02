package com.careerflow.backend.dto.interview;

import com.careerflow.backend.entity.RoundStatus;
import com.careerflow.backend.entity.RoundType;

import java.time.LocalDateTime;

public record InterviewRoundResponse(
        Long id,
        Long applicationId,
        String roundTitle,
        RoundType roundType,
        RoundStatus status,
        LocalDateTime scheduledTime,
        String meetingLink,
        String feedback,
        LocalDateTime createdAt
) {}
