package com.cognizant.Ticket_service.controller;

import com.cognizant.Ticket_service.entity.Ticket;
import com.cognizant.Ticket_service.entity.TicketContributor;
import com.cognizant.Ticket_service.repository.TicketContributorRepository;
import com.cognizant.Ticket_service.repository.TicketRepository;
import com.cognizant.Ticket_service.service.TicketService;
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
    public ResponseEntity<Ticket> getTicketInternal(@PathVariable("ticketId") UUID ticketId) {
        Ticket ticket = ticketService.getTicketById(ticketId);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/users/{userId}/tickets")
    public ResponseEntity<List<Ticket>> getTicketsByUser(@PathVariable("userId") UUID userId) {
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
