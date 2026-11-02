package com.careerflow.backend.repository;

import com.careerflow.backend.entity.Application;
import com.careerflow.backend.entity.ApplicationStatus;
import com.careerflow.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ApplicationRepository
 * 
 * Demonstrates:
 * - JpaSpecificationExecutor for flexible, dynamic predicates (filtering)
 * - Custom JPQL aggregations for dashboard metrics
 * - User-scoped querying preventing IDOR (Insecure Direct Object Reference) vulnerabilities
 */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long>, JpaSpecificationExecutor<Application> {

    Optional<Application> findByIdAndUser(Long id, User user);

    Page<Application> findAllByUser(User user, Pageable pageable);

    @Query("SELECT a.status, COUNT(a) FROM Application a WHERE a.user = :user GROUP BY a.status")
    List<Object[]> countApplicationsByStatusGroup(@Param("user") User user);

    long countByUser(User user);

    long countByUserAndStatus(User user, ApplicationStatus status);
}
