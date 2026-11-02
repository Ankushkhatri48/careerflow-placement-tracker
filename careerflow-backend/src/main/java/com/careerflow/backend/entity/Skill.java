package com.careerflow.backend.entity;

import jakarta.persistence.*;

/**
 * Skill Entity (Normalized skills catalog)
 */
@Entity
@Table(
        name = "skills",
        indexes = {
                @Index(name = "idx_skills_name", columnList = "name", unique = true)
        }
)
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String name;

    public Skill() {
    }

    public Skill(String name) {
        this.name = name != null ? name.trim().toLowerCase() : null;
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
        this.name = name != null ? name.trim().toLowerCase() : null;
    }
}
