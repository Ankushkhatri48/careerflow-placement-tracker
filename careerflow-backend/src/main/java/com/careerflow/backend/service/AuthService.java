package com.careerflow.backend.service;

import com.careerflow.backend.dto.auth.AuthResponse;
import com.careerflow.backend.dto.auth.LoginRequest;
import com.careerflow.backend.dto.auth.RefreshTokenRequest;
import com.careerflow.backend.dto.auth.RegisterRequest;
import com.careerflow.backend.dto.auth.UserDto;

public interface AuthService {

    UserDto register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    UserDto getCurrentUser(String email);
}
