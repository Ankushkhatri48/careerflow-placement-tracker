package com.careerflow.backend.repository;

import com.careerflow.backend.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    Page<Company> findByNameContainingIgnoreCaseOrIndustryContainingIgnoreCase(String name, String industry, Pageable pageable);
}
