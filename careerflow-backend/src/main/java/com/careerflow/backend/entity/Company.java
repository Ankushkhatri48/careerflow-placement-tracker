package com.careerflow.backend.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Company Entity
 * 
 * Demonstrates:
 * - Master catalog of employers
 * - Unique index on company name
 * - Bidirectional One-to-Many relationship with Application
 */
@Entity
@Table(
        name = "companies",
        indexes = {
                @Index(name = "idx_companies_name", columnList = "name", unique = true),
                @Index(name = "idx_companies_industry", columnList = "industry")
        }
)
public class Company extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @Column(length = 255)
    private String website;

    @Column(length = 100)
    private String location;

    @Column(length = 100)
    private String industry;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private List<Application> applications = new ArrayList<>();

    public Company() {
    }

    public Company(String name, String website, String location, String industry) {
        this.name = name;
        this.website = website;
        this.location = location;
        this.industry = industry;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }
}
