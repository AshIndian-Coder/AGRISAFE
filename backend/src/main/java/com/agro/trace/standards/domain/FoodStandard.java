package com.agro.trace.standards.domain;

import com.agro.trace.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "food_standards")
@Getter
@Setter
@NoArgsConstructor
public class FoodStandard extends BaseEntity {

    @Column(name = "standard_name", nullable = false, length = 255)
    private String standardName;

    @Column(name = "regulation_name", length = 500)
    private String regulationName;

    @Column(name = "chapter", length = 255)
    private String chapter;

    @Column(name = "section_reference", length = 255)
    private String sectionReference;

    @Column(name = "source_url", length = 1000)
    private String sourceUrl;

    @Column(name = "source_document", length = 500)
    private String sourceDocument;

    @Column(name = "standard_version", nullable = false, length = 50)
    private String standardVersion;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "amendment_reference", length = 255)
    private String amendmentReference;

    @Column(name = "retrieved_at")
    private LocalDate retrievedAt;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}