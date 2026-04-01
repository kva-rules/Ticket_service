package com.cognizant.Ticket_service.controller;

import com.cognizant.Ticket_service.dto.request.TicketContributorRequestDTO;
import com.cognizant.Ticket_service.entity.TicketContributor;
import com.cognizant.Ticket_service.service.ContributorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/contributors")
public class ContributorController {

    private final ContributorService contributorService;

    public ContributorController(ContributorService contributorService) {
        this.contributorService = contributorService;
    }

    @PostMapping
    public ResponseEntity<TicketContributor> addContributor(@RequestBody TicketContributorRequestDTO request) {
        TicketContributor contributor = contributorService.addContributor(request.getTicketId(), request.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(contributor);
    }

    @GetMapping
    public ResponseEntity<List<TicketContributor>> getAllContributors() {
        List<TicketContributor> contributors = contributorService.getAllContributors();
        return ResponseEntity.ok(contributors);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteContributorsByUserId(@PathVariable("userId") UUID userId) {
        contributorService.removeContributorsByUserId(userId);
        return ResponseEntity.noContent().build();
    }
}
