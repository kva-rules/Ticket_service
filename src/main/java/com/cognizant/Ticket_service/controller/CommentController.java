package com.cognizant.Ticket_service.controller;

import com.cognizant.Ticket_service.dto.request.TicketCommentRequestDTO;
import com.cognizant.Ticket_service.entity.TicketComment;
import com.cognizant.Ticket_service.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<TicketComment> createComment(@RequestBody TicketCommentRequestDTO request) {
        TicketComment comment = commentService.addComment(request.getTicketId(), request.getUserId(), request.getCommentText());
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @GetMapping
    public ResponseEntity<List<TicketComment>> getComments() {
        List<TicketComment> comments = commentService.getAllComments();
        return ResponseEntity.ok(comments);
    }
}
