package com.careerflow.backend.service;

import com.careerflow.backend.dto.application.ApplicationRequest;
import com.careerflow.backend.dto.application.ApplicationResponse;
import com.careerflow.backend.dto.application.StatusUpdateRequest;
import com.careerflow.backend.entity.Application;
import com.careerflow.backend.entity.ApplicationStatus;
import com.careerflow.backend.entity.Company;
import com.careerflow.backend.entity.Role;
import com.careerflow.backend.entity.User;
import com.careerflow.backend.exception.ResourceNotFoundException;
import com.careerflow.backend.repository.ApplicationRepository;
import com.careerflow.backend.repository.CompanyRepository;
import com.careerflow.backend.repository.SkillRepository;
import com.careerflow.backend.repository.UserRepository;
import com.careerflow.backend.service.impl.ApplicationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private User testUser;
    private Company testCompany;

    @BeforeEach
    void setUp() {
        testUser = new User("Jane Doe", "jane@test.com", "pass123", Role.ROLE_USER);
        testUser.setId(10L);

        testCompany = new Company("Google", "https://google.com", "Mountain View, CA", "Tech");
        testCompany.setId(20L);
    }

    @Test
    @DisplayName("Should create application successfully with user ownership")
    void createApplication_Success() {
        ApplicationRequest request = new ApplicationRequest(
                20L,
                "Software Engineer",
                "https://careers.google.com/job/123",
                "Remote",
                new BigDecimal("120000"),
                new BigDecimal("160000"),
                ApplicationStatus.APPLIED,
                LocalDate.now(),
                "Referral application",
                Set.of("Java", "Spring Boot")
        );

        when(userRepository.findByEmail("jane@test.com")).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(20L)).thenReturn(Optional.of(testCompany));
        when(skillRepository.findByNameIgnoreCase(anyString())).thenAnswer(inv -> Optional.of(new com.careerflow.backend.entity.Skill(inv.getArgument(0))));
        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> {
            Application a = invocation.getArgument(0);
            a.setId(100L);
            return a;
        });

        ApplicationResponse response = applicationService.createApplication("jane@test.com", request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.jobTitle()).isEqualTo("Software Engineer");
        assertThat(response.company().name()).isEqualTo("Google");
        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    @DisplayName("Should update application status successfully")
    void updateStatus_Success() {
        Application application = new Application(testUser, testCompany, "Backend Dev", "NYC", LocalDate.now());
        application.setId(100L);
        application.setStatus(ApplicationStatus.APPLIED);

        when(userRepository.findByEmail("jane@test.com")).thenReturn(Optional.of(testUser));
        when(applicationRepository.findByIdAndUser(100L, testUser)).thenReturn(Optional.of(application));
        when(applicationRepository.save(any(Application.class))).thenAnswer(inv -> inv.getArgument(0));

        StatusUpdateRequest statusRequest = new StatusUpdateRequest(ApplicationStatus.TECHNICAL);
        ApplicationResponse response = applicationService.updateStatus("jane@test.com", 100L, statusRequest);

        assertThat(response.status()).isEqualTo(ApplicationStatus.TECHNICAL);
        verify(applicationRepository).save(application);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when application does not belong to user")
    void getApplicationById_NotFound_ThrowsException() {
        when(userRepository.findByEmail("jane@test.com")).thenReturn(Optional.of(testUser));
        when(applicationRepository.findByIdAndUser(999L, testUser)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationService.getApplicationById("jane@test.com", 999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
