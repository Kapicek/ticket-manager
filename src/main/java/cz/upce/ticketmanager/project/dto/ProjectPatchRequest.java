package cz.upce.ticketmanager.project.dto;

import cz.upce.ticketmanager.project.ProjectStatus;

public record ProjectPatchRequest(
        String name,
        String description,
        ProjectStatus status
) {}
