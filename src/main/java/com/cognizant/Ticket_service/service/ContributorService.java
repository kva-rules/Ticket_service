package com.cognizant.Ticket_service.service;

import com.cognizant.Ticket_service.entity.TicketContributor;

import java.util.List;
import java.util.UUID;

public interface ContributorService {

    TicketContributor addContributor(UUID ticketId, UUID userId);

    List<TicketContributor> getAllContributors();

    void removeContributorsByUserId(UUID userId);
}
