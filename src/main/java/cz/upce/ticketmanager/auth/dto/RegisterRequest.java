package cz.upce.ticketmanager.auth.dto;

import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank @Size(min=3, max=60) String username,
        @NotBlank @Email @Size(max=160) String email,
        @NotBlank @Size(min=6, max=120) String password
) {}
