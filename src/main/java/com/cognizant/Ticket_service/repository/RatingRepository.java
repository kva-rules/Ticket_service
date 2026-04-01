package com.cognizant.Ticket_service.repository;

import com.cognizant.Ticket_service.entity.TicketRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<TicketRating, UUID> {
}
