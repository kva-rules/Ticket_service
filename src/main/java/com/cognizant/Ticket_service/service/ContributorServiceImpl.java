package com.cognizant.Ticket_service.service;

import com.cognizant.Ticket_service.entity.Ticket;
import com.cognizant.Ticket_service.entity.TicketContributor;
import com.cognizant.Ticket_service.exception.ResourceNotFoundException;
import com.cognizant.Ticket_service.exception.ValidationException;
import com.cognizant.Ticket_service.repository.TicketContributorRepository;
import com.cognizant.Ticket_service.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ContributorServiceImpl implements ContributorService {

    private final TicketContributorRepository contributorRepository;
    private final TicketRepository ticketRepository;

    public ContributorServiceImpl(
            TicketContributorRepository contributorRepository,
            TicketRepository ticketRepository
    ) {
        this.contributorRepository = contributorRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    @Transactional
    public TicketContributor addContributor(UUID ticketId, UUID userId) {
        if (ticketId == null) {
            throw new ValidationException("ticketId must not be null");
        }
        if (userId == null) {
            throw new ValidationException("userId must not be null");
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketId));

        Optional<TicketContributor> existing = contributorRepository.findByTicketIdAndUserId(ticketId, userId);
        if (existing.isPresent()) {
            return existing.get();
        }

        TicketContributor contributor = new TicketContributor();
        contributor.setTicketId(ticket.getTicketId());
        contributor.setUserId(userId);
        contributor.setJoinedAt(LocalDateTime.now());
        return contributorRepository.save(contributor);
    }

    @Override
    public List<TicketContributor> getAllContributors() {
        return contributorRepository.findAll();
    }

    @Override
    @Transactional
    public void removeContributorsByUserId(UUID userId) {
        if (userId == null) {
            throw new ValidationException("userId must not be null");
        }
        contributorRepository.deleteByUserId(userId);
    }
}
