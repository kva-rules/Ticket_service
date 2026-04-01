package com.cognizant.Ticket_service.dto.request;

import java.util.UUID;

public class TicketStatusRequestDTO {

    private UUID ticketId;
    private String status;

    public TicketStatusRequestDTO() {
    }

    public UUID getTicketId() {
        return ticketId;
    }

    public void setTicketId(UUID ticketId) {
        this.ticketId = ticketId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
