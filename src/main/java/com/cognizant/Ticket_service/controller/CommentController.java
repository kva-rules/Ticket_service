package com.cognizant.Ticket_service.controller;

import com.cognizant.Ticket_service.dto.request.TicketCommentRequestDTO;
import com.cognizant.Ticket_service.entity.TicketComment;
import com.cognizant.Ticket_service.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@Tag(name = "Ticket Comments", description = "Comments on a ticket thread")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    @Operation(summary = "Add a comment", description = "Posts a new comment on a ticket thread")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Comment created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<TicketComment> createComment(@RequestBody TicketCommentRequestDTO request) {
        TicketComment comment = commentService.addComment(request.getTicketId(), request.getUserId(), request.getCommentText());
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @GetMapping
    @Operation(summary = "List all comments", description = "Returns every ticket comment in the system")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comments returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "No comments found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<TicketComment>> getComments() {
        List<TicketComment> comments = commentService.getAllComments();
        return ResponseEntity.ok(comments);
    }
}
