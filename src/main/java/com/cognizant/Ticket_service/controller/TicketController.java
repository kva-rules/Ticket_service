package com.cognizant.Ticket_service.controller;

import com.cognizant.Ticket_service.dto.request.TicketAssignRequestDTO;
import com.cognizant.Ticket_service.dto.request.TicketRequestDTO;
import com.cognizant.Ticket_service.dto.request.TicketResolveRequestDTO;
import com.cognizant.Ticket_service.dto.request.TicketStatusRequestDTO;
import com.cognizant.Ticket_service.dto.response.TicketStatisticsDTO;
import com.cognizant.Ticket_service.entity.Ticket;
import com.cognizant.Ticket_service.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody TicketRequestDTO request) {
        Ticket created = ticketService.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable("id") UUID ticketId) {
        Ticket ticket = ticketService.getTicketById(ticketId);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Ticket>> searchTickets(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) String difficultyLevel,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String assignedTo
    ) {
        List<Ticket> tickets = ticketService.searchTickets(title, category, difficultyLevel, status, assignedTo);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/statistics")
    public ResponseEntity<TicketStatisticsDTO> getStatistics() {
        TicketStatisticsDTO stats = ticketService.getTicketStatistics();
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(
            @PathVariable("id") UUID ticketId,
            @RequestBody TicketRequestDTO request
    ) {
        Ticket updated = ticketService.updateTicket(ticketId, request);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{ticketId}/assign")
    public ResponseEntity<Ticket> assignTicket(
            @PathVariable("ticketId") UUID ticketId,
            @RequestBody TicketAssignRequestDTO request
    ) {
        Ticket assigned = ticketService.assignTicket(ticketId, request.getAssignedTo());
        return ResponseEntity.ok(assigned);
    }

    @PutMapping("/status")
    public ResponseEntity<Ticket> updateTicketStatus(@RequestBody TicketStatusRequestDTO request) {
        Ticket updated = ticketService.updateStatus(request.getTicketId(), request.getStatus());
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/resolve")
    public ResponseEntity<Ticket> resolveTicket(@RequestBody TicketResolveRequestDTO request) {
        Ticket resolved = ticketService.resolveTicket(request.getTicketId());
        return ResponseEntity.ok(resolved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable("id") UUID ticketId) {
        ticketService.deleteTicket(ticketId);
        return ResponseEntity.noContent().build();
    }
}
