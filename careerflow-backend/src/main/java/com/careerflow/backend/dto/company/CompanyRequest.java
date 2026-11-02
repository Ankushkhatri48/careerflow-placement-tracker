package com.careerflow.backend.dto.company;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompanyRequest(
        @NotBlank(message = "Company name is required")
        @Size(min = 2, max = 120, message = "Company name must be between 2 and 120 characters")
        String name,

        String website,
        String location,
        String industry,
        String logoUrl
) {}
