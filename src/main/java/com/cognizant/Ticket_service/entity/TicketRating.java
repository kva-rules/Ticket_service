package com.cognizant.Ticket_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ticket_ratings")
public class TicketRating {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID ratingId;

    @Column(nullable = false)
    private UUID ticketId;

    @Column(nullable = false)
    private UUID ratedBy;

    private Integer rating;

    @Column(length = 2000)
    private String feedback;

    private LocalDateTime createdAt;

    public TicketRating() {
    }

    public UUID getRatingId() {
        return ratingId;
    }

    public void setRatingId(UUID ratingId) {
        this.ratingId = ratingId;
    }

    public UUID getTicketId() {
        return ticketId;
    }

    public void setTicketId(UUID ticketId) {
        this.ticketId = ticketId;
    }

    public UUID getRatedBy() {
        return ratedBy;
    }

    public void setRatedBy(UUID ratedBy) {
        this.ratedBy = ratedBy;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
