package com.cognizant.Ticket_service.dto.request;

import java.util.UUID;

public class TicketCommentRequestDTO {

    private UUID ticketId;
    private UUID userId;
    private String commentText;

    public TicketCommentRequestDTO() {
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

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
}
