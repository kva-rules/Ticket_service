package com.cognizant.Ticket_service.service;

import com.cognizant.Ticket_service.dto.request.TicketRequestDTO;
import com.cognizant.Ticket_service.dto.response.TicketStatisticsDTO;
import com.cognizant.Ticket_service.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface TicketService {

    Ticket createTicket(TicketRequestDTO ticketRequestDTO);

    Ticket updateTicket(UUID ticketId, TicketRequestDTO ticketRequestDTO);

    void deleteTicket(UUID ticketId);

    Ticket getTicketById(UUID ticketId);

    Page<Ticket> getAllTickets(Pageable pageable);

    Page<Ticket> getMyTickets(String createdBy, Pageable pageable);

    Ticket assignTicket(UUID ticketId, String assignedTo);

    Ticket updateStatus(UUID ticketId, String status);

    Ticket resolveTicket(UUID ticketId);

    Ticket rateTicket(UUID ticketId, Integer rating, String feedback, UUID ratedBy);

    Page<Ticket> searchTickets(String title, Long categoryId, String difficultyLevel,
                               String status, String assignedTo, Pageable pageable);

    List<Ticket> searchTickets(String query);

    TicketStatisticsDTO getTicketStatistics();
}
