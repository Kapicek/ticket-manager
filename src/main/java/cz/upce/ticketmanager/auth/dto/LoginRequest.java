package cz.upce.ticketmanager.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String login,   // username nebo email
        @NotBlank String password
) {}
