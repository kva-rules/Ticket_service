package com.cognizant.Ticket_service.service;

import com.cognizant.Ticket_service.entity.TicketComment;

import java.util.List;
import java.util.UUID;

public interface CommentService {

    TicketComment addComment(UUID ticketId, UUID userId, String commentText);

    List<TicketComment> getCommentsByTicketId(UUID ticketId);

    List<TicketComment> getAllComments();
}
