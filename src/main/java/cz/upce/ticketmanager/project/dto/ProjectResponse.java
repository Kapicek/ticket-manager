package cz.upce.ticketmanager.project.dto;

import cz.upce.ticketmanager.project.ProjectStatus;

public record ProjectResponse(
        Long id,
        String name,
        String description,
        ProjectStatus status
) {}
