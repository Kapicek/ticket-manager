package cz.upce.ticketmanager.ticket.dto;

import cz.upce.ticketmanager.ticket.TicketPriority;
import cz.upce.ticketmanager.ticket.TicketState;
import cz.upce.ticketmanager.ticket.TicketType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TicketRequest(
        @NotBlank @Size(min = 1, max = 160) String title,
        TicketType type,
        TicketPriority priority,
        TicketState state,
        Long assigneeId
) {}
