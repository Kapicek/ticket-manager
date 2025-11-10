package cz.upce.ticketmanager.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentRequest(
        @NotBlank @Size(min=1, max=5000) String body,
        Long ticketId
) {}