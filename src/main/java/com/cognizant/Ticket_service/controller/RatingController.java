package com.cognizant.Ticket_service.controller;

import com.cognizant.Ticket_service.dto.request.TicketRatingRequestDTO;
import com.cognizant.Ticket_service.entity.Ticket;
import com.cognizant.Ticket_service.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Ticket Ratings", description = "Post-resolution feedback and ratings")
public class RatingController {

    private final TicketService ticketService;

    public RatingController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/rating")
    @Operation(summary = "Submit a ticket rating", description = "Records a user rating and feedback for a resolved ticket")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Rating submitted"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Ticket> submitRating(@RequestBody TicketRatingRequestDTO request) {
        Ticket ticket = ticketService.rateTicket(request.getTicketId(), request.getRating(), request.getFeedback());
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }
}
