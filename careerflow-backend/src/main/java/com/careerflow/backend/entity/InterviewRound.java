package com.careerflow.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * InterviewRound Entity
 * 
 * Tracks interview stages, scheduled date/time, and feedback notes.
 */
@Entity
@Table(
        name = "interview_rounds",
        indexes = {
                @Index(name = "idx_interview_app_time", columnList = "application_id, scheduled_time")
        }
)
public class InterviewRound extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Column(name = "round_title", nullable = false, length = 120)
    private String roundTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "round_type", nullable = false, length = 30)
    private RoundType roundType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoundStatus status = RoundStatus.SCHEDULED;

    @Column(name = "scheduled_time", nullable = false)
    private LocalDateTime scheduledTime;

    @Column(name = "meeting_link", length = 500)
    private String meetingLink;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    public InterviewRound() {
    }

    public InterviewRound(Application application, String roundTitle, RoundType roundType, LocalDateTime scheduledTime) {
        this.application = application;
        this.roundTitle = roundTitle;
        this.roundType = roundType;
        this.scheduledTime = scheduledTime;
        this.status = RoundStatus.SCHEDULED;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public String getRoundTitle() {
        return roundTitle;
    }

    public void setRoundTitle(String roundTitle) {
        this.roundTitle = roundTitle;
    }

    public RoundType getRoundType() {
        return roundType;
    }

    public void setRoundType(RoundType roundType) {
        this.roundType = roundType;
    }

    public RoundStatus getStatus() {
        return status;
    }

    public void setStatus(RoundStatus status) {
        this.status = status;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    public void setMeetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
