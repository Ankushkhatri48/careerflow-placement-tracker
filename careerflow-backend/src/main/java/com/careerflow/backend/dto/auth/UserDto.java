package com.careerflow.backend.dto.auth;

public record UserDto(
        Long id,
        String fullName,
        String email,
        String role
) {}
