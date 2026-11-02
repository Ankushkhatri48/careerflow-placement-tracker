package com.careerflow.backend.service;

import com.careerflow.backend.dto.auth.AuthResponse;
import com.careerflow.backend.dto.auth.LoginRequest;
import com.careerflow.backend.dto.auth.RegisterRequest;
import com.careerflow.backend.dto.auth.UserDto;
import com.careerflow.backend.entity.Role;
import com.careerflow.backend.entity.User;
import com.careerflow.backend.exception.DuplicateResourceException;
import com.careerflow.backend.repository.RefreshTokenRepository;
import com.careerflow.backend.repository.UserRepository;
import com.careerflow.backend.security.JwtUtils;
import com.careerflow.backend.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(
                userRepository,
                refreshTokenRepository,
                passwordEncoder,
                authenticationManager,
                jwtUtils,
                604800000L
        );
    }

    @Test
    @DisplayName("Should successfully register a new user with hashed password")
    void register_Success() {
        RegisterRequest request = new RegisterRequest("Test Candidate", "candidate@test.com", "Password@123");
        when(userRepository.existsByEmail("candidate@test.com")).thenReturn(false);
        when(passwordEncoder.encode("Password@123")).thenReturn("hashedPassword123");

        User savedUser = new User("Test Candidate", "candidate@test.com", "hashedPassword123", Role.ROLE_USER);
        savedUser.setId(1L);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDto result = authService.register(request);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.email()).isEqualTo("candidate@test.com");
        assertThat(result.fullName()).isEqualTo("Test Candidate");
        assertThat(result.role()).isEqualTo("ROLE_USER");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when registering existing email")
    void register_DuplicateEmail_ThrowsException() {
        RegisterRequest request = new RegisterRequest("Test Candidate", "existing@test.com", "Password@123");
        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should authenticate and return JWT token on valid credentials")
    void login_Success() {
        LoginRequest request = new LoginRequest("candidate@test.com", "Password@123");
        User user = new User("Test Candidate", "candidate@test.com", "hashedPassword123", Role.ROLE_USER);
        user.setId(1L);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("candidate@test.com", null));
        when(userRepository.findByEmail("candidate@test.com")).thenReturn(Optional.of(user));
        when(jwtUtils.generateToken("candidate@test.com", "ROLE_USER")).thenReturn("mocked.jwt.token");
        when(refreshTokenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("mocked.jwt.token");
        assertThat(response.user().email()).isEqualTo("candidate@test.com");
    }

    @Test
    @DisplayName("Should fail authentication on invalid password")
    void login_BadCredentials_ThrowsException() {
        LoginRequest request = new LoginRequest("candidate@test.com", "WrongPassword");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }
}
