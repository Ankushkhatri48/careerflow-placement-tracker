package com.careerflow.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * CareerFlow Backend Main Application
 * 
 * Demonstrates:
 * - Spring Boot 3 autoconfiguration
 * - JPA Auditing for automatic @CreatedDate and @LastModifiedDate management
 */
@SpringBootApplication
@EnableJpaAuditing
public class CareerflowBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CareerflowBackendApplication.class, args);
    }
}
