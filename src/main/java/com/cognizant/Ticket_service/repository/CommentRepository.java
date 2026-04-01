package com.cognizant.Ticket_service.repository;

import com.cognizant.Ticket_service.entity.TicketComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<TicketComment, UUID> {
}
