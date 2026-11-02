package com.careerflow.backend.service.impl;

import com.careerflow.backend.dto.application.ApplicationRequest;
import com.careerflow.backend.dto.application.ApplicationResponse;
import com.careerflow.backend.dto.application.DashboardStatsResponse;
import com.careerflow.backend.dto.application.StatusUpdateRequest;
import com.careerflow.backend.dto.common.PageResponse;
import com.careerflow.backend.dto.company.CompanyResponse;
import com.careerflow.backend.entity.*;
import com.careerflow.backend.exception.ResourceNotFoundException;
import com.careerflow.backend.repository.ApplicationRepository;
import com.careerflow.backend.repository.CompanyRepository;
import com.careerflow.backend.repository.SkillRepository;
import com.careerflow.backend.repository.UserRepository;
import com.careerflow.backend.service.ApplicationService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final SkillRepository skillRepository;

    public ApplicationServiceImpl(
            ApplicationRepository applicationRepository,
            UserRepository userRepository,
            CompanyRepository companyRepository,
            SkillRepository skillRepository
    ) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.skillRepository = skillRepository;
    }

    @Override
    @Transactional
    public ApplicationResponse createApplication(String userEmail, ApplicationRequest request) {
        User user = getUser(userEmail);
        Company company = getCompany(request.companyId());

        Application application = new Application(
                user,
                company,
                request.jobTitle().trim(),
                request.jobLocation(),
                request.appliedDate()
        );

        application.setJobUrl(request.jobUrl());
        application.setSalaryMin(request.salaryMin());
        application.setSalaryMax(request.salaryMax());
        if (request.status() != null) {
            application.setStatus(request.status());
        }
        application.setNotes(request.notes());

        if (request.skills() != null && !request.skills().isEmpty()) {
            application.setSkills(resolveSkills(request.skills()));
        }

        Application saved = applicationRepository.save(application);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ApplicationResponse updateApplication(String userEmail, Long id, ApplicationRequest request) {
        User user = getUser(userEmail);
        Application application = applicationRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", id));

        Company company = getCompany(request.companyId());

        application.setCompany(company);
        application.setJobTitle(request.jobTitle().trim());
        application.setJobUrl(request.jobUrl());
        application.setJobLocation(request.jobLocation());
        application.setSalaryMin(request.salaryMin());
        application.setSalaryMax(request.salaryMax());
        if (request.status() != null) {
            application.setStatus(request.status());
        }
        if (request.appliedDate() != null) {
            application.setAppliedDate(request.appliedDate());
        }
        application.setNotes(request.notes());

        if (request.skills() != null) {
            application.setSkills(resolveSkills(request.skills()));
        }

        Application updated = applicationRepository.save(application);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public ApplicationResponse updateStatus(String userEmail, Long id, StatusUpdateRequest request) {
        User user = getUser(userEmail);
        Application application = applicationRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", id));

        application.setStatus(request.status());
        Application updated = applicationRepository.save(application);
        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationById(String userEmail, Long id) {
        User user = getUser(userEmail);
        Application application = applicationRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", id));
        return mapToResponse(application);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ApplicationResponse> getApplications(
            String userEmail,
            String search,
            ApplicationStatus status,
            Long companyId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        User user = getUser(userEmail);

        Specification<Application> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always enforce user isolation
            predicates.add(cb.equal(root.get("user"), user));

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (companyId != null) {
                predicates.add(cb.equal(root.get("company").get("id"), companyId));
            }

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("appliedDate"), startDate));
            }

            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("appliedDate"), endDate));
            }

            if (StringUtils.hasText(search)) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                Join<Application, Company> companyJoin = root.join("company");
                Predicate titlePredicate = cb.like(cb.lower(root.get("jobTitle")), pattern);
                Predicate locationPredicate = cb.like(cb.lower(root.get("jobLocation")), pattern);
                Predicate companyPredicate = cb.like(cb.lower(companyJoin.get("name")), pattern);

                predicates.add(cb.or(titlePredicate, locationPredicate, companyPredicate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Application> page = applicationRepository.findAll(spec, pageable);
        Page<ApplicationResponse> dtoPage = page.map(this::mapToResponse);
        return PageResponse.from(dtoPage);
    }

    @Override
    @Transactional
    public void deleteApplication(String userEmail, Long id) {
        User user = getUser(userEmail);
        Application application = applicationRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", id));
        applicationRepository.delete(application);
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats(String userEmail) {
        User user = getUser(userEmail);

        long total = applicationRepository.countByUser(user);
        long interviews = applicationRepository.countByUserAndStatus(user, ApplicationStatus.TECHNICAL)
                + applicationRepository.countByUserAndStatus(user, ApplicationStatus.HR)
                + applicationRepository.countByUserAndStatus(user, ApplicationStatus.SCREENING);
        long offers = applicationRepository.countByUserAndStatus(user, ApplicationStatus.OFFER);
        long rejections = applicationRepository.countByUserAndStatus(user, ApplicationStatus.REJECTED);

        List<Object[]> statusGroups = applicationRepository.countApplicationsByStatusGroup(user);
        Map<String, Long> statusCounts = new HashMap<>();
        for (Object[] row : statusGroups) {
            ApplicationStatus s = (ApplicationStatus) row[0];
            Long count = (Long) row[1];
            statusCounts.put(s.name(), count);
        }

        return new DashboardStatsResponse(total, interviews, offers, rejections, statusCounts);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private Company getCompany(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));
    }

    private Set<Skill> resolveSkills(Set<String> skillNames) {
        Set<Skill> skills = new HashSet<>();
        for (String name : skillNames) {
            if (StringUtils.hasText(name)) {
                String clean = name.trim().toLowerCase();
                Skill skill = skillRepository.findByNameIgnoreCase(clean)
                        .orElseGet(() -> skillRepository.save(new Skill(clean)));
                skills.add(skill);
            }
        }
        return skills;
    }

    private ApplicationResponse mapToResponse(Application app) {
        Company c = app.getCompany();
        CompanyResponse compResp = new CompanyResponse(
                c.getId(),
                c.getName(),
                c.getWebsite(),
                c.getLocation(),
                c.getIndustry(),
                c.getLogoUrl(),
                0,
                c.getCreatedAt()
        );

        Set<String> skillNames = app.getSkills() != null
                ? app.getSkills().stream().map(Skill::getName).collect(Collectors.toSet())
                : Collections.emptySet();

        int roundCount = app.getInterviewRounds() != null ? app.getInterviewRounds().size() : 0;

        return new ApplicationResponse(
                app.getId(),
                compResp,
                app.getJobTitle(),
                app.getJobUrl(),
                app.getJobLocation(),
                app.getSalaryMin(),
                app.getSalaryMax(),
                app.getStatus(),
                app.getAppliedDate(),
                app.getNotes(),
                skillNames,
                roundCount,
                app.getCreatedAt(),
                app.getUpdatedAt()
        );
    }
}
