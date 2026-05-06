package com.cognizant.Ticket_service.repository;

import com.cognizant.Ticket_service.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    Page<Ticket> findByDeletedFalse(Pageable pageable);

    Page<Ticket> findByStatusAndDeletedFalse(String status, Pageable pageable);

    Page<Ticket> findByCreatedByAndDeletedFalse(String createdBy, Pageable pageable);

    List<Ticket> findByAssignedToAndDeletedFalse(String assignedTo);

    @Query(value = "SELECT * FROM tickets t WHERE t.deleted = false " +
           "AND (CAST(:title AS text) IS NULL OR LOWER(t.title) LIKE '%' || LOWER(CAST(:title AS text)) || '%') " +
           "AND (CAST(:categoryId AS bigint) IS NULL OR t.category_id = CAST(:categoryId AS bigint)) " +
           "AND (CAST(:difficultyLevel AS text) IS NULL OR t.difficulty_level = CAST(:difficultyLevel AS text)) " +
           "AND (CAST(:status AS text) IS NULL OR t.status = CAST(:status AS text)) " +
           "AND (CAST(:assignedTo AS text) IS NULL OR LOWER(t.assigned_to) LIKE '%' || LOWER(CAST(:assignedTo AS text)) || '%')",
           countQuery = "SELECT COUNT(*) FROM tickets t WHERE t.deleted = false " +
           "AND (CAST(:title AS text) IS NULL OR LOWER(t.title) LIKE '%' || LOWER(CAST(:title AS text)) || '%') " +
           "AND (CAST(:categoryId AS bigint) IS NULL OR t.category_id = CAST(:categoryId AS bigint)) " +
           "AND (CAST(:difficultyLevel AS text) IS NULL OR t.difficulty_level = CAST(:difficultyLevel AS text)) " +
           "AND (CAST(:status AS text) IS NULL OR t.status = CAST(:status AS text)) " +
           "AND (CAST(:assignedTo AS text) IS NULL OR LOWER(t.assigned_to) LIKE '%' || LOWER(CAST(:assignedTo AS text)) || '%')",
           nativeQuery = true)
    Page<Ticket> searchTickets(
            @Param("title") String title,
            @Param("categoryId") Long categoryId,
            @Param("difficultyLevel") String difficultyLevel,
            @Param("status") String status,
            @Param("assignedTo") String assignedTo,
            Pageable pageable);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.deleted = false AND t.status = :status")
    long countByStatusAndDeletedFalse(@Param("status") String status);
}
