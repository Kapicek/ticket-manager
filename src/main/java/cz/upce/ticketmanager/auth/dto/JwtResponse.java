package cz.upce.ticketmanager.auth.dto;

public record JwtResponse(String token, long expiresInSeconds) {}
