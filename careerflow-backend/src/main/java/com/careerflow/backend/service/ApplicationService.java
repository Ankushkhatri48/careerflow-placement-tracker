package com.careerflow.backend.service;

import com.careerflow.backend.dto.application.ApplicationRequest;
import com.careerflow.backend.dto.application.ApplicationResponse;
import com.careerflow.backend.dto.application.DashboardStatsResponse;
import com.careerflow.backend.dto.application.StatusUpdateRequest;
import com.careerflow.backend.dto.common.PageResponse;
import com.careerflow.backend.entity.ApplicationStatus;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ApplicationService {

    ApplicationResponse createApplication(String userEmail, ApplicationRequest request);

    ApplicationResponse updateApplication(String userEmail, Long id, ApplicationRequest request);

    ApplicationResponse updateStatus(String userEmail, Long id, StatusUpdateRequest request);

    ApplicationResponse getApplicationById(String userEmail, Long id);

    PageResponse<ApplicationResponse> getApplications(
            String userEmail,
            String search,
            ApplicationStatus status,
            Long companyId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

    void deleteApplication(String userEmail, Long id);

    DashboardStatsResponse getDashboardStats(String userEmail);
}
