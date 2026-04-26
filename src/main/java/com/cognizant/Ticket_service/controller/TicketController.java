package com.cognizant.Ticket_service.controller;

import com.cognizant.Ticket_service.dto.request.TicketAssignRequestDTO;
import com.cognizant.Ticket_service.dto.request.TicketRequestDTO;
import com.cognizant.Ticket_service.dto.request.TicketResolveRequestDTO;
import com.cognizant.Ticket_service.dto.request.TicketStatusRequestDTO;
import com.cognizant.Ticket_service.dto.response.TicketStatisticsDTO;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Tickets", description = "Create/list/update/close support tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    @Operation(summary = "Create a new ticket", description = "Opens a new support ticket from the request body")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ticket created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Ticket> createTicket(@RequestBody TicketRequestDTO request) {
        Ticket created = ticketService.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a ticket by id", description = "Returns the ticket matching the given UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ticket found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Ticket not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Ticket> getTicketById(
            @Parameter(description = "UUID of the ticket") @PathVariable("id") UUID ticketId) {
        Ticket ticket = ticketService.getTicketById(ticketId);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping
    @Operation(summary = "List all tickets", description = "Returns every ticket in the system")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tickets returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "No tickets found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Ticket>> getAllTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/search")
    @Operation(summary = "Search tickets", description = "Filter tickets by title, category, difficulty, status, assignee")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "No matches"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Ticket>> searchTickets(
            @Parameter(description = "Partial ticket title") @RequestParam(required = false) String title,
            @Parameter(description = "Category id") @RequestParam(required = false) Long category,
            @Parameter(description = "Difficulty level") @RequestParam(required = false) String difficultyLevel,
            @Parameter(description = "Ticket status") @RequestParam(required = false) String status,
            @Parameter(description = "Assignee user id") @RequestParam(required = false) String assignedTo
    ) {
        List<Ticket> tickets = ticketService.searchTickets(title, category, difficultyLevel, status, assignedTo);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get ticket statistics", description = "Returns aggregate counts and metrics for tickets")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statistics returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "No data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<TicketStatisticsDTO> getStatistics() {
        TicketStatisticsDTO stats = ticketService.getTicketStatistics();
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a ticket", description = "Updates ticket fields for the given id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ticket updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Ticket not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Ticket> updateTicket(
            @Parameter(description = "UUID of the ticket") @PathVariable("id") UUID ticketId,
            @RequestBody TicketRequestDTO request
    ) {
        Ticket updated = ticketService.updateTicket(ticketId, request);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{ticketId}/assign")
    @Operation(summary = "Assign a ticket", description = "Assigns the ticket to the given engineer")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ticket assigned"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Ticket> assignTicket(
            @Parameter(description = "UUID of the ticket") @PathVariable("ticketId") UUID ticketId,
            @RequestBody TicketAssignRequestDTO request
    ) {
        Ticket assigned = ticketService.assignTicket(ticketId, request.getAssignedTo());
        return ResponseEntity.ok(assigned);
    }

    @PutMapping("/status")
    @Operation(summary = "Update ticket status", description = "Changes the status of a ticket")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Ticket not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Ticket> updateTicketStatus(@RequestBody TicketStatusRequestDTO request) {
        Ticket updated = ticketService.updateStatus(request.getTicketId(), request.getStatus());
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/resolve")
    @Operation(summary = "Resolve a ticket", description = "Marks the given ticket as resolved")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ticket resolved"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Ticket not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Ticket> resolveTicket(@RequestBody TicketResolveRequestDTO request) {
        Ticket resolved = ticketService.resolveTicket(request.getTicketId());
        return ResponseEntity.ok(resolved);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a ticket", description = "Permanently removes the ticket for the given id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Ticket deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Ticket not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteTicket(
            @Parameter(description = "UUID of the ticket") @PathVariable("id") UUID ticketId) {
        ticketService.deleteTicket(ticketId);
        return ResponseEntity.noContent().build();
    }
}
