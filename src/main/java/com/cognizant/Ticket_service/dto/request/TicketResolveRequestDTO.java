package com.cognizant.Ticket_service.dto.request;

import java.util.UUID;

public class TicketResolveRequestDTO {

    private UUID ticketId;

    public TicketResolveRequestDTO() {
    }

    public UUID getTicketId() {
        return ticketId;
    }

    public void setTicketId(UUID ticketId) {
        this.ticketId = ticketId;
    }
}
