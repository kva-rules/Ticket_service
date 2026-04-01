package com.cognizant.Ticket_service.service;

import com.cognizant.Ticket_service.entity.TicketComment;
import com.cognizant.Ticket_service.exception.ResourceNotFoundException;
import com.cognizant.Ticket_service.exception.ValidationException;
import com.cognizant.Ticket_service.repository.CommentRepository;
import com.cognizant.Ticket_service.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;

    public CommentServiceImpl(CommentRepository commentRepository, TicketRepository ticketRepository) {
        this.commentRepository = commentRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    @Transactional
    public TicketComment addComment(UUID ticketId, UUID userId, String commentText) {
        if (ticketId == null) {
            throw new ValidationException("ticketId must not be null");
        }
        if (userId == null) {
            throw new ValidationException("userId must not be null");
        }
        if (!StringUtils.hasText(commentText)) {
            throw new ValidationException("commentText must not be empty");
        }

        ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketId));

        TicketComment comment = new TicketComment();
        comment.setTicketId(ticketId);
        comment.setUserId(userId);
        comment.setCommentText(commentText.trim());
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    @Override
    public List<TicketComment> getCommentsByTicketId(UUID ticketId) {
        if (ticketId == null) {
            throw new ValidationException("ticketId must not be null");
        }
        ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketId));
        return commentRepository.findByTicketId(ticketId);
    }

    @Override
    public List<TicketComment> getAllComments() {
        return commentRepository.findAll();
    }
}
