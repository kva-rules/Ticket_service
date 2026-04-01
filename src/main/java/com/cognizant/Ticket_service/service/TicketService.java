package com.cognizant.Ticket_service.service;

import com.cognizant.Ticket_service.dto.request.TicketRequestDTO;
import com.cognizant.Ticket_service.dto.response.TicketResponseDTO;
import com.cognizant.Ticket_service.entity.Ticket;

import java.util.List;
import java.util.UUID;

public interface TicketService {

    Ticket createTicket(TicketRequestDTO ticketRequestDTO);

    Ticket updateTicket(UUID ticketId, TicketRequestDTO ticketRequestDTO);

    void deleteTicket(UUID ticketId);

    Ticket getTicketById(UUID ticketId);

    List<Ticket> getAllTickets();

    Ticket assignTicket(UUID ticketId, String assignedTo);

    Ticket updateStatus(UUID ticketId, String status);

    Ticket resolveTicket(UUID ticketId);

    Ticket rateTicket(UUID ticketId, Integer rating, String feedback);

    List<Ticket> searchTickets(String query);

    List<Ticket> searchTickets(String title,
                               Long categoryId,
                               String difficultyLevel,
                               String status,
                               String assignedTo);
}
