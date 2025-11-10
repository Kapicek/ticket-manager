package cz.upce.ticketmanager.ticket.dto;

import cz.upce.ticketmanager.ticket.TicketPriority;
import cz.upce.ticketmanager.ticket.TicketState;
import cz.upce.ticketmanager.ticket.TicketType;

public record TicketPatchRequest(
        String title,
        TicketType type,
        TicketPriority priority,
        TicketState state,
        Long assigneeId
) {}

