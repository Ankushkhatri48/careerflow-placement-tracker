package com.careerflow.backend.service.impl;

import com.careerflow.backend.dto.common.PageResponse;
import com.careerflow.backend.dto.company.CompanyRequest;
import com.careerflow.backend.dto.company.CompanyResponse;
import com.careerflow.backend.entity.Company;
import com.careerflow.backend.exception.DuplicateResourceException;
import com.careerflow.backend.exception.ResourceNotFoundException;
import com.careerflow.backend.repository.CompanyRepository;
import com.careerflow.backend.service.CompanyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    @Transactional
    public CompanyResponse createCompany(CompanyRequest request) {
        String trimmedName = request.name().trim();
        if (companyRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new DuplicateResourceException("Company with name '" + trimmedName + "' already exists");
        }

        Company company = new Company(
                trimmedName,
                request.website(),
                request.location(),
                request.industry()
        );
        company.setLogoUrl(request.logoUrl());

        Company saved = companyRepository.save(company);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public CompanyResponse updateCompany(Long id, CompanyRequest request) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));

        String trimmedName = request.name().trim();
        if (!company.getName().equalsIgnoreCase(trimmedName) && companyRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new DuplicateResourceException("Company with name '" + trimmedName + "' already exists");
        }

        company.setName(trimmedName);
        company.setWebsite(request.website());
        company.setLocation(request.location());
        company.setIndustry(request.industry());
        company.setLogoUrl(request.logoUrl());

        Company updated = companyRepository.save(company);
        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyResponse getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
        return mapToResponse(company);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CompanyResponse> getAllCompanies(String search, Pageable pageable) {
        Page<Company> page;
        if (StringUtils.hasText(search)) {
            String query = search.trim();
            page = companyRepository.findByNameContainingIgnoreCaseOrIndustryContainingIgnoreCase(query, query, pageable);
        } else {
            page = companyRepository.findAll(pageable);
        }

        Page<CompanyResponse> dtoPage = page.map(this::mapToResponse);
        return PageResponse.from(dtoPage);
    }

    @Override
    @Transactional
    public void deleteCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
        companyRepository.delete(company);
    }

    private CompanyResponse mapToResponse(Company company) {
        long appCount = company.getApplications() != null ? company.getApplications().size() : 0;
        return new CompanyResponse(
                company.getId(),
                company.getName(),
                company.getWebsite(),
                company.getLocation(),
                company.getIndustry(),
                company.getLogoUrl(),
                appCount,
                company.getCreatedAt()
        );
    }
}
