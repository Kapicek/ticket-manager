package cz.upce.ticketmanager.project.dto;

import cz.upce.ticketmanager.project.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectRequest(
        @NotBlank @Size(min = 1, max = 120) String name,
        String description,
        ProjectStatus status
) {}
