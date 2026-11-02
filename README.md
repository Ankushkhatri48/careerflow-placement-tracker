# CareerFlow: Job Application & Placement Management System

A production-grade, enterprise Full-Stack Placement Management application engineered with **Java 21**, **Spring Boot 3.3.4**, **Spring Security 6**, **JWT (JJWT 0.12.6)**, **Spring Data JPA**, **Hibernate**, **MySQL 8**, and an AI-assisted **React 18** dashboard.

Designed specifically to demonstrate backend software engineering competencies for SDE-1 and Java Backend Developer placement interviews.

---

## 🌟 Key Architecture & Highlights

- **Clean Layered Architecture**: `Controller` → `Service` → `Repository` → `MySQL Database`.
- **Decoupled API Contracts**: Strict DTO usage (Java Records & standard classes) with zero JPA entity leakage to API clients.
- **Robust Security**:
  - Stateless Spring Security 6 filter chain with `OncePerRequestFilter` (`JwtAuthenticationFilter`).
  - Short-lived Access Token + Database-persisted Refresh Token rotation mechanism.
  - BCrypt password hashing.
  - Multi-tenant user data isolation preventing IDOR vulnerabilities.
- **Relational MySQL 8 Design**:
  - Normalized schema (`users`, `refresh_tokens`, `companies`, `applications`, `interview_rounds`, `skills`, `application_skills`).
  - Strict Foreign Keys, unique constraints, and composite indexes for high-throughput querying.
  - Automatic timestamp auditing with `@EnableJpaAuditing` and `@MappedSuperclass`.
- **Dynamic Search & Filtering**: Spring Data JPA `Specification` API (`JpaSpecificationExecutor`) allowing dynamic multi-predicate queries (title, company, status, date range, salary).
- **Pagination & Sorting**: Decoupled `PageResponse<T>` wrapper over `Pageable` & `Sort`.
- **Unified Global Exception Handling**: Centralized `@RestControllerAdvice` mapping validation errors (`MethodArgumentNotValidException`), bad credentials, and resource conflicts into standard `ErrorResponse`.
- **API Documentation**: Live interactive OpenAPI 3 / Swagger UI at `/swagger-ui.html`.
- **Automated Testing**: JUnit 5 and Mockito unit tests validating business services and edge cases.
- **Frontend Dashboard**: Responsive React + Tailwind CSS dashboard with Recharts pipeline funnel visualizations and real-time backend API consumption.

---

## 📂 Project Structure

```
SB Project/
├── ARCHITECTURE_PLAN.md                 # System architecture & entity relationship diagram
├── BACKEND_INTERVIEW_GUIDE.md           # In-depth technical interview Q&A and internal workflows
├── DEPLOYMENT_GUIDE.md                  # Oracle Cloud Always Free + Docker + Nginx deployment guide
├── careerflow_postman_collection.json   # Exported Postman collection for all REST endpoints
│
├── careerflow-backend/                  # Spring Boot 3 + Java 21 REST API
│   ├── pom.xml
│   ├── schema.sql                       # MySQL DDL schema and seed data
│   └── src/
│       ├── main/java/com/careerflow/backend/
│       │   ├── config/                  # SecurityConfig, OpenApiConfig
│       │   ├── controller/              # AuthController, CompanyController, ApplicationController, InterviewRoundController
│       │   ├── dto/                     # Request and Response record DTOs
│       │   ├── entity/                  # JPA Entities (User, Company, Application, InterviewRound, Skill)
│       │   ├── exception/               # GlobalExceptionHandler and custom exceptions
│       │   ├── repository/              # Spring Data JPA Repositories
│       │   ├── security/                # JwtUtils, JwtAuthenticationFilter, CustomUserDetailsService
│       │   └── service/                 # Service interfaces and transactional implementations
│       └── test/java/com/careerflow/backend/ # JUnit 5 + Mockito test suites
│
└── careerflow-frontend/                 # React 18 + Tailwind CSS + Axios + Recharts
    ├── package.json
    ├── vite.config.js
    └── src/
        ├── api/client.js                # Axios instance with Bearer token interceptor
        ├── context/AuthContext.jsx      # Global authentication provider
        ├── components/Layout.jsx        # Sidebar navigation & header
        └── pages/                       # AuthPage, DashboardPage, ApplicationsPage, CompaniesPage
```

---

## 🚀 Getting Started

### 1. Prerequisites
- **Java 21 LTS** or later
- **Maven 3.9+**
- **Node.js 18+** & npm
- **MySQL 8.0+**

### 2. Database Setup
Create database and initialize tables using `schema.sql`:
```bash
mysql -u root -p < careerflow-backend/schema.sql
```

### 3. Running Backend
```bash
cd careerflow-backend
mvn spring-boot:run
```
- API Base URL: `http://localhost:8080/api/v1`
- Swagger UI Documentation: `http://localhost:8080/swagger-ui.html`

### 4. Running Backend Tests
```bash
cd careerflow-backend
mvn test
```

### 5. Running Frontend
```bash
cd careerflow-frontend
npm run dev
```
Open `http://localhost:5173` in your browser.

---

## 📘 Interview Preparation
Refer to [BACKEND_INTERVIEW_GUIDE.md](file:///c:/Users/Jaat/Desktop/SB%20Project/BACKEND_INTERVIEW_GUIDE.md) for detailed explanations of:
1. Spring Boot request lifecycle from Tomcat through SecurityFilterChain to DispatcherServlet.
2. Spring IoC, Bean lifecycle, and Dependency Injection mechanisms.
3. Hibernate Dirty Checking, First-Level Cache, and `@Transactional` propagation.
4. JWT token generation, HMAC-SHA256 signature verification, and SecurityContextHolder propagation.
5. JPA Specification pattern vs native SQL vs JPQL.
