package com.cognizant.Ticket_service.controller;

import com.cognizant.Ticket_service.entity.Ticket;
import com.cognizant.Ticket_service.entity.TicketContributor;
import com.cognizant.Ticket_service.repository.TicketContributorRepository;
import com.cognizant.Ticket_service.repository.TicketRepository;
import com.cognizant.Ticket_service.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/internal")
@Tag(name = "Tickets (Internal)", description = "Service-to-service ticket lookups (not for public use)")
public class InternalTicketController {

    private final TicketService ticketService;
    private final TicketRepository ticketRepository;
    private final TicketContributorRepository contributorRepository;

    public InternalTicketController(
            TicketService ticketService,
            TicketRepository ticketRepository,
            TicketContributorRepository contributorRepository
    ) {
        this.ticketService = ticketService;
        this.ticketRepository = ticketRepository;
        this.contributorRepository = contributorRepository;
    }

    @GetMapping("/tickets/{ticketId}")
    @Operation(summary = "Get ticket (internal)", description = "Fetches a ticket for service-to-service calls")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ticket found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Ticket not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Ticket> getTicketInternal(
            @Parameter(description = "UUID of the ticket") @PathVariable("ticketId") UUID ticketId) {
        Ticket ticket = ticketService.getTicketById(ticketId);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/users/{userId}/tickets")
    @Operation(summary = "Get tickets for user (internal)", description = "Returns tickets assigned to or contributed by a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tickets returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Ticket>> getTicketsByUser(
            @Parameter(description = "UUID of the user") @PathVariable("userId") UUID userId) {
        List<Ticket> assignedTickets = ticketRepository.findByAssignedTo(userId.toString());
        List<TicketContributor> contributors = contributorRepository.findByUserId(userId);

        Set<UUID> ticketIds = new HashSet<>();
        List<Ticket> results = new ArrayList<>();

        for (Ticket ticket : assignedTickets) {
            if (ticketIds.add(ticket.getTicketId())) {
                results.add(ticket);
            }
        }

        for (TicketContributor contributor : contributors) {
            Ticket ticket = ticketService.getTicketById(contributor.getTicketId());
            if (ticketIds.add(ticket.getTicketId())) {
                results.add(ticket);
            }
        }

        return ResponseEntity.ok(results);
    }
}
