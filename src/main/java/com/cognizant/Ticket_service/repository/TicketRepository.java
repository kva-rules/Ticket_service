package com.cognizant.Ticket_service.repository;

import com.cognizant.Ticket_service.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    List<Ticket> findByStatus(String status);

    List<Ticket> findByCategoryId(Long categoryId);

    List<Ticket> findByAssignedTo(String assignedTo);
}
