package com.cognizant.Ticket_service.dto.request;

public class TicketAssignRequestDTO {

    private String assignedTo;

    public TicketAssignRequestDTO() {
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }
}
