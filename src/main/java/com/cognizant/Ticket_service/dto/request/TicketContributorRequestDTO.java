package com.cognizant.Ticket_service.dto.request;

import java.util.UUID;

public class TicketContributorRequestDTO {

    private UUID ticketId;
    private UUID userId;

    public TicketContributorRequestDTO() {
    }

    public UUID getTicketId() {
        return ticketId;
    }

    public void setTicketId(UUID ticketId) {
        this.ticketId = ticketId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
