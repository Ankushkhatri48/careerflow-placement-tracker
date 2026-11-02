package com.careerflow.backend.repository;

import com.careerflow.backend.entity.Application;
import com.careerflow.backend.entity.InterviewRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewRoundRepository extends JpaRepository<InterviewRound, Long> {

    List<InterviewRound> findAllByApplicationOrderByScheduledTimeAsc(Application application);
}
