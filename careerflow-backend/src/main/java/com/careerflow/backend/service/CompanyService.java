package com.careerflow.backend.service;

import com.careerflow.backend.dto.common.PageResponse;
import com.careerflow.backend.dto.company.CompanyRequest;
import com.careerflow.backend.dto.company.CompanyResponse;
import org.springframework.data.domain.Pageable;

public interface CompanyService {

    CompanyResponse createCompany(CompanyRequest request);

    CompanyResponse updateCompany(Long id, CompanyRequest request);

    CompanyResponse getCompanyById(Long id);

    PageResponse<CompanyResponse> getAllCompanies(String search, Pageable pageable);

    void deleteCompany(Long id);
}
