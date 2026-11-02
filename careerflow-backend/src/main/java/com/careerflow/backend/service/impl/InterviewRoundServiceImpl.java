package com.careerflow.backend.service.impl;

import com.careerflow.backend.dto.interview.InterviewRoundRequest;
import com.careerflow.backend.dto.interview.InterviewRoundResponse;
import com.careerflow.backend.entity.Application;
import com.careerflow.backend.entity.InterviewRound;
import com.careerflow.backend.entity.RoundStatus;
import com.careerflow.backend.entity.User;
import com.careerflow.backend.exception.ResourceNotFoundException;
import com.careerflow.backend.repository.ApplicationRepository;
import com.careerflow.backend.repository.InterviewRoundRepository;
import com.careerflow.backend.repository.UserRepository;
import com.careerflow.backend.service.InterviewRoundService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InterviewRoundServiceImpl implements InterviewRoundService {

    private final InterviewRoundRepository interviewRoundRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public InterviewRoundServiceImpl(
            InterviewRoundRepository interviewRoundRepository,
            ApplicationRepository applicationRepository,
            UserRepository userRepository
    ) {
        this.interviewRoundRepository = interviewRoundRepository;
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public InterviewRoundResponse addRound(String userEmail, Long applicationId, InterviewRoundRequest request) {
        Application application = getOwnedApplication(userEmail, applicationId);

        InterviewRound round = new InterviewRound(
                application,
                request.roundTitle().trim(),
                request.roundType(),
                request.scheduledTime()
        );
        if (request.status() != null) {
            round.setStatus(request.status());
        }
        round.setMeetingLink(request.meetingLink());
        round.setFeedback(request.feedback());

        InterviewRound saved = interviewRoundRepository.save(round);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public InterviewRoundResponse updateRound(String userEmail, Long applicationId, Long roundId, InterviewRoundRequest request) {
        Application application = getOwnedApplication(userEmail, applicationId);
        InterviewRound round = interviewRoundRepository.findById(roundId)
                .filter(r -> r.getApplication().getId().equals(application.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("InterviewRound", "id", roundId));

        round.setRoundTitle(request.roundTitle().trim());
        round.setRoundType(request.roundType());
        if (request.status() != null) {
            round.setStatus(request.status());
        }
        round.setScheduledTime(request.scheduledTime());
        round.setMeetingLink(request.meetingLink());
        round.setFeedback(request.feedback());

        InterviewRound updated = interviewRoundRepository.save(round);
        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewRoundResponse> getRoundsForApplication(String userEmail, Long applicationId) {
        Application application = getOwnedApplication(userEmail, applicationId);
        return interviewRoundRepository.findAllByApplicationOrderByScheduledTimeAsc(application)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteRound(String userEmail, Long applicationId, Long roundId) {
        Application application = getOwnedApplication(userEmail, applicationId);
        InterviewRound round = interviewRoundRepository.findById(roundId)
                .filter(r -> r.getApplication().getId().equals(application.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("InterviewRound", "id", roundId));

        interviewRoundRepository.delete(round);
    }

    private Application getOwnedApplication(String userEmail, Long applicationId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));
        return applicationRepository.findByIdAndUser(applicationId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));
    }

    private InterviewRoundResponse mapToResponse(InterviewRound r) {
        return new InterviewRoundResponse(
                r.getId(),
                r.getApplication().getId(),
                r.getRoundTitle(),
                r.getRoundType(),
                r.getStatus(),
                r.getScheduledTime(),
                r.getMeetingLink(),
                r.getFeedback(),
                r.getCreatedAt()
        );
    }
}
