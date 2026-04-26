package com.cognizant.Ticket_service.controller;

import com.cognizant.Ticket_service.dto.request.TicketContributorRequestDTO;
import com.cognizant.Ticket_service.entity.TicketContributor;
import com.cognizant.Ticket_service.service.ContributorService;
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
@RequestMapping("/contributors")
@Tag(name = "Ticket Contributors", description = "Engineers and collaborators working on a ticket")
public class ContributorController {

    private final ContributorService contributorService;

    public ContributorController(ContributorService contributorService) {
        this.contributorService = contributorService;
    }

    @PostMapping
    @Operation(summary = "Add a contributor", description = "Associates a user as contributor on a ticket")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contributor added"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<TicketContributor> addContributor(@RequestBody TicketContributorRequestDTO request) {
        TicketContributor contributor = contributorService.addContributor(request.getTicketId(), request.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(contributor);
    }

    @GetMapping
    @Operation(summary = "List all contributors", description = "Returns every ticket contributor entry")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contributors returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "No contributors found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<TicketContributor>> getAllContributors() {
        List<TicketContributor> contributors = contributorService.getAllContributors();
        return ResponseEntity.ok(contributors);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Remove contributor by user", description = "Deletes all contributor rows for the given user id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contributors removed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteContributorsByUserId(
            @Parameter(description = "UUID of the user") @PathVariable("userId") UUID userId) {
        contributorService.removeContributorsByUserId(userId);
        return ResponseEntity.noContent().build();
    }
}
