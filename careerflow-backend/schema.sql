-- CareerFlow Database Initialization Script
-- MySQL 8.0+ compatible

CREATE DATABASE IF NOT EXISTS careerflow_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE careerflow_db;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_email (email)
) ENGINE=InnoDB;

-- 2. Refresh Tokens Table (Rotation & Session control)
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(128) NOT NULL UNIQUE,
    expiry_date DATETIME(6) NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_refresh_tokens_token (token)
) ENGINE=InnoDB;

-- 3. Companies Table (Catalog of employers)
CREATE TABLE IF NOT EXISTS companies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL UNIQUE,
    website VARCHAR(255) NULL,
    location VARCHAR(100) NULL,
    industry VARCHAR(100) NULL,
    logo_url VARCHAR(500) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_companies_name (name),
    INDEX idx_companies_industry (industry)
) ENGINE=InnoDB;

-- 4. Applications Table (Core aggregate)
CREATE TABLE IF NOT EXISTS applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    company_id BIGINT NOT NULL,
    job_title VARCHAR(150) NOT NULL,
    job_url VARCHAR(500) NULL,
    job_location VARCHAR(120) NULL,
    salary_min DECIMAL(12, 2) NULL,
    salary_max DECIMAL(12, 2) NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'APPLIED',
    applied_date DATE NOT NULL,
    notes TEXT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_applications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_applications_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE RESTRICT,
    INDEX idx_applications_user_status (user_id, status),
    INDEX idx_applications_user_date (user_id, applied_date),
    INDEX idx_applications_company (company_id)
) ENGINE=InnoDB;

-- 5. Interview Rounds Table
CREATE TABLE IF NOT EXISTS interview_rounds (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    round_title VARCHAR(120) NOT NULL,
    round_type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    scheduled_time DATETIME NOT NULL,
    meeting_link VARCHAR(500) NULL,
    feedback TEXT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_interview_rounds_application FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE,
    INDEX idx_interview_app_time (application_id, scheduled_time)
) ENGINE=InnoDB;

-- 6. Skills Table
CREATE TABLE IF NOT EXISTS skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(60) NOT NULL UNIQUE,
    INDEX idx_skills_name (name)
) ENGINE=InnoDB;

-- 7. Application Skills Join Table (Many-to-Many)
CREATE TABLE IF NOT EXISTS application_skills (
    application_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    PRIMARY KEY (application_id, skill_id),
    CONSTRAINT fk_app_skills_application FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE,
    CONSTRAINT fk_app_skills_skill FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Sample Seed Data
INSERT IGNORE INTO companies (id, name, website, location, industry) VALUES
(1, 'Google', 'https://careers.google.com', 'Mountain View, CA / Bengaluru, India', 'Technology / Cloud'),
(2, 'Amazon', 'https://amazon.jobs', 'Seattle, WA / Hyderabad, India', 'E-Commerce / Cloud'),
(3, 'Microsoft', 'https://careers.microsoft.com', 'Redmond, WA / Noida, India', 'Software / AI'),
(4, 'Atlassian', 'https://www.atlassian.com/company/careers', 'Sydney / Bengaluru, India', 'Enterprise Software'),
(5, 'Stripe', 'https://stripe.com/jobs', 'San Francisco / Remote', 'Fintech / Payments');
