package com.cognizant.Ticket_service.controller;

import com.cognizant.Ticket_service.dto.request.TicketRatingRequestDTO;
import com.cognizant.Ticket_service.entity.Ticket;
import com.cognizant.Ticket_service.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RatingController {

    private final TicketService ticketService;

    public RatingController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/rating")
    public ResponseEntity<Ticket> submitRating(@RequestBody TicketRatingRequestDTO request) {
        Ticket ticket = ticketService.rateTicket(request.getTicketId(), request.getRating(), request.getFeedback());
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }
}
