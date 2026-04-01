package com.cognizant.Ticket_service.dto.response;

public class TicketStatisticsDTO {

    private long totalTickets;
    private long openTickets;
    private long resolvedTickets;
    private double averageRating;

    public TicketStatisticsDTO() {
    }

    public TicketStatisticsDTO(long totalTickets, long openTickets, long resolvedTickets, double averageRating) {
        this.totalTickets = totalTickets;
        this.openTickets = openTickets;
        this.resolvedTickets = resolvedTickets;
        this.averageRating = averageRating;
    }

    public long getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(long totalTickets) {
        this.totalTickets = totalTickets;
    }

    public long getOpenTickets() {
        return openTickets;
    }

    public void setOpenTickets(long openTickets) {
        this.openTickets = openTickets;
    }

    public long getResolvedTickets() {
        return resolvedTickets;
    }

    public void setResolvedTickets(long resolvedTickets) {
        this.resolvedTickets = resolvedTickets;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
}
