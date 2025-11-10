package cz.upce.ticketmanager.comment.dto;

import java.time.Instant;

public record CommentResponse(
        Long id, String body, Long authorId, Long ticketId, Instant createdAt
) {}
