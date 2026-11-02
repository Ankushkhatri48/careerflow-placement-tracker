package com.careerflow.backend.service;

import com.careerflow.backend.dto.interview.InterviewRoundRequest;
import com.careerflow.backend.dto.interview.InterviewRoundResponse;

import java.util.List;

public interface InterviewRoundService {

    InterviewRoundResponse addRound(String userEmail, Long applicationId, InterviewRoundRequest request);

    InterviewRoundResponse updateRound(String userEmail, Long applicationId, Long roundId, InterviewRoundRequest request);

    List<InterviewRoundResponse> getRoundsForApplication(String userEmail, Long applicationId);

    void deleteRound(String userEmail, Long applicationId, Long roundId);
}
