package com.agro.trace.complaints.dto;

import jakarta.validation.constraints.NotBlank;

public record ComplaintRequest(
        @NotBlank(message = "Category is required")
        String category,

        @NotBlank(message = "Description is required")
        String description,

        String relatedLotId,

        Long relatedOrganizationId,

        String evidenceJson
) {}