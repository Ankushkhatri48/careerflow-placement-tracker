package com.careerflow.backend.service.impl;

import com.careerflow.backend.dto.auth.AuthResponse;
import com.careerflow.backend.dto.auth.LoginRequest;
import com.careerflow.backend.dto.auth.RefreshTokenRequest;
import com.careerflow.backend.dto.auth.RegisterRequest;
import com.careerflow.backend.dto.auth.UserDto;
import com.careerflow.backend.entity.RefreshToken;
import com.careerflow.backend.entity.Role;
import com.careerflow.backend.entity.User;
import com.careerflow.backend.exception.BadRequestException;
import com.careerflow.backend.exception.DuplicateResourceException;
import com.careerflow.backend.exception.ResourceNotFoundException;
import com.careerflow.backend.repository.RefreshTokenRepository;
import com.careerflow.backend.repository.UserRepository;
import com.careerflow.backend.security.JwtUtils;
import com.careerflow.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final long refreshTokenExpirationMs;

    public AuthServiceImpl(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtUtils jwtUtils,
            @Value("${app.jwt.refresh-expiration-ms:604800000}") long refreshTokenExpirationMs // 7 days
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    @Override
    @Transactional
    public UserDto register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email().trim().toLowerCase())) {
            throw new DuplicateResourceException("An account with email '" + request.email() + "' already exists");
        }

        User user = new User(
                request.fullName().trim(),
                request.email().trim().toLowerCase(),
                passwordEncoder.encode(request.password()),
                Role.ROLE_USER
        );

        User savedUser = userRepository.save(user);
        return new UserDto(savedUser.getId(), savedUser.getFullName(), savedUser.getEmail(), savedUser.getRole().name());
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().trim().toLowerCase(), request.password())
        );

        User user = userRepository.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.email()));

        String accessToken = jwtUtils.generateToken(user.getEmail(), user.getRole().name());
        RefreshToken refreshToken = createRefreshToken(user);

        UserDto userDto = new UserDto(user.getId(), user.getFullName(), user.getEmail(), user.getRole().name());
        return new AuthResponse(accessToken, refreshToken.getToken(), userDto);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken token = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        if (token.isRevoked() || token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new BadRequestException("Refresh token was expired or revoked. Please log in again.");
        }

        User user = token.getUser();
        String newAccessToken = jwtUtils.generateToken(user.getEmail(), user.getRole().name());

        UserDto userDto = new UserDto(user.getId(), user.getFullName(), user.getEmail(), user.getRole().name());
        return new AuthResponse(newAccessToken, token.getToken(), userDto);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return new UserDto(user.getId(), user.getFullName(), user.getEmail(), user.getRole().name());
    }

    private RefreshToken createRefreshToken(User user) {
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = new RefreshToken(
                user,
                UUID.randomUUID().toString(),
                Instant.now().plusMillis(refreshTokenExpirationMs)
        );

        return refreshTokenRepository.save(refreshToken);
    }
}
