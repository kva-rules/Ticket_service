package com.cognizant.Ticket_service.dto.request;

import java.util.UUID;

public class TicketRatingRequestDTO {

    private UUID ticketId;
    private Integer rating;
    private String feedback;

    public TicketRatingRequestDTO() {
    }

    public UUID getTicketId() {
        return ticketId;
    }

    public void setTicketId(UUID ticketId) {
        this.ticketId = ticketId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
