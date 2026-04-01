package com.cognizant.Ticket_service.repository;

import com.cognizant.Ticket_service.entity.TicketContributor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketContributorRepository extends JpaRepository<TicketContributor, UUID> {

    Optional<TicketContributor> findByTicketIdAndUserId(UUID ticketId, UUID userId);

    List<TicketContributor> findByTicketId(UUID ticketId);

    List<TicketContributor> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}
