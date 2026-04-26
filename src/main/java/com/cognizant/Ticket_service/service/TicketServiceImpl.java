package com.cognizant.Ticket_service.service;

import com.cognizant.Ticket_service.dto.request.TicketRequestDTO;
import com.cognizant.Ticket_service.dto.response.TicketStatisticsDTO;
import com.cognizant.Ticket_service.entity.Category;
import com.cognizant.Ticket_service.entity.DifficultyLevel;
import com.cognizant.Ticket_service.entity.Priority;
import com.cognizant.Ticket_service.entity.Status;
import com.cognizant.Ticket_service.entity.Ticket;
import com.cognizant.Ticket_service.entity.TicketContributor;
import com.cognizant.Ticket_service.exception.ResourceNotFoundException;
import com.cognizant.Ticket_service.exception.ValidationException;
import com.cognizant.Ticket_service.repository.CategoryRepository;
import com.cognizant.Ticket_service.repository.RatingRepository;
import com.cognizant.Ticket_service.repository.TicketContributorRepository;
import com.cognizant.Ticket_service.repository.TicketRepository;
import com.library.common.event.TicketCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl implements TicketService {

    private static final String EVENT_TOPIC = "ticket-events";
    private static final String TICKET_CREATED_TOPIC = "ticket.created";

    private static final Map<Status, Set<Status>> VALID_STATUS_TRANSITIONS = Map.of(
            Status.OPEN, Set.of(Status.IN_PROGRESS, Status.RESOLVED, Status.CLOSED),
            Status.IN_PROGRESS, Set.of(Status.RESOLVED, Status.CLOSED, Status.REOPENED),
            Status.RESOLVED, Set.of(Status.CLOSED, Status.REOPENED),
            Status.CLOSED, Set.of(Status.REOPENED),
            Status.REOPENED, Set.of(Status.IN_PROGRESS, Status.RESOLVED, Status.CLOSED)
    );

    private final TicketRepository ticketRepository;
    private final CategoryRepository categoryRepository;
    private final RatingRepository ratingRepository;
    private final TicketContributorRepository contributorRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TicketServiceImpl(
            TicketRepository ticketRepository,
            CategoryRepository categoryRepository,
            RatingRepository ratingRepository,
            TicketContributorRepository contributorRepository,
            KafkaTemplate<String, Object> kafkaTemplate
    ) {
        this.ticketRepository = ticketRepository;
        this.categoryRepository = categoryRepository;
        this.ratingRepository = ratingRepository;
        this.contributorRepository = contributorRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Transactional
    public Ticket createTicket(TicketRequestDTO ticketRequestDTO) {
        validateTicketRequest(ticketRequestDTO);
        Category category = categoryRepository.findById(ticketRequestDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + ticketRequestDTO.getCategoryId()));

        Ticket ticket = new Ticket();
        ticket.setTitle(ticketRequestDTO.getTitle().trim());
        ticket.setDescription(ticketRequestDTO.getDescription().trim());
        ticket.setCategoryId(category.getCategoryId());
        ticket.setDifficultyLevel(normalizeEnumValue(ticketRequestDTO.getDifficultyLevel(), DifficultyLevel.class));
        ticket.setPriority(normalizeEnumValue(ticketRequestDTO.getPriority(), Priority.class));
        ticket.setVisibility(normalizeEnumValue(ticketRequestDTO.getVisibility(), com.cognizant.Ticket_service.entity.Visibility.class));
        ticket.setStatus(Status.OPEN.name());
        ticket.setCreatedBy(currentPrincipalName());
        ticket.setDeleted(false);

        Ticket saved = ticketRepository.save(ticket);
        publishEvent("ticket.created", saved, "Ticket created");
        publishTicketCreatedEvent(saved);
        return saved;
    }

    private void publishTicketCreatedEvent(Ticket ticket) {
        try {
            Long assignedUserId = null;
            if (ticket.getAssignedTo() != null) {
                try {
                    assignedUserId = Long.parseLong(ticket.getAssignedTo());
                } catch (NumberFormatException e) {
                    // assignedTo might be a UUID or username, try to parse as UUID hash
                }
            }
            Long creatorUserId = null;
            if (ticket.getCreatedBy() != null) {
                try {
                    creatorUserId = Long.parseLong(ticket.getCreatedBy());
                } catch (NumberFormatException e) {
                    // createdBy might be a username
                }
            }
            TicketCreatedEvent event = TicketCreatedEvent.builder()
                    .ticketId(ticket.getTicketId() != null ? ticket.getTicketId().getMostSignificantBits() : null)
                    .title(ticket.getTitle())
                    .assignedUserId(assignedUserId)
                    .creatorUserId(creatorUserId)
                    .difficulty(ticket.getDifficultyLevel())
                    .build();
            kafkaTemplate.send(TICKET_CREATED_TOPIC, event);
        } catch (Exception e) {
            // Log error but don't fail the ticket creation
        }
    }

    @Override
    @Transactional
    public Ticket updateTicket(UUID ticketId, TicketRequestDTO ticketRequestDTO) {
        Ticket ticket = findExistingTicket(ticketId);
        if (StringUtils.hasText(ticketRequestDTO.getTitle())) {
            ticket.setTitle(ticketRequestDTO.getTitle().trim());
        }
        if (StringUtils.hasText(ticketRequestDTO.getDescription())) {
            ticket.setDescription(ticketRequestDTO.getDescription().trim());
        }
        if (ticketRequestDTO.getCategoryId() != null) {
            categoryRepository.findById(ticketRequestDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + ticketRequestDTO.getCategoryId()));
            ticket.setCategoryId(ticketRequestDTO.getCategoryId());
        }
        if (StringUtils.hasText(ticketRequestDTO.getDifficultyLevel())) {
            ticket.setDifficultyLevel(normalizeEnumValue(ticketRequestDTO.getDifficultyLevel(), DifficultyLevel.class));
        }
        if (StringUtils.hasText(ticketRequestDTO.getPriority())) {
            ticket.setPriority(normalizeEnumValue(ticketRequestDTO.getPriority(), Priority.class));
        }
        if (StringUtils.hasText(ticketRequestDTO.getVisibility())) {
            ticket.setVisibility(normalizeEnumValue(ticketRequestDTO.getVisibility(), com.cognizant.Ticket_service.entity.Visibility.class));
        }

        Ticket updated = ticketRepository.save(ticket);
        publishEvent("ticket.updated", updated, "Ticket updated");
        return updated;
    }

    @Override
    @Transactional
    public void deleteTicket(UUID ticketId) {
        Ticket ticket = findExistingTicket(ticketId);
        ticket.setDeleted(true);
        ticketRepository.save(ticket);
    }

    @Override
    public Ticket getTicketById(UUID ticketId) {
        return findExistingTicket(ticketId);
    }

    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getDeleted() == null || !ticket.getDeleted())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Ticket assignTicket(UUID ticketId, String assignedTo) {
        if (!StringUtils.hasText(assignedTo)) {
            throw new ValidationException("assignedTo must not be empty");
        }
        Ticket ticket = findExistingTicket(ticketId);
        ticket.setAssignedTo(assignedTo.trim());
        if (Status.OPEN.name().equals(ticket.getStatus())) {
            ticket.setStatus(Status.IN_PROGRESS.name());
        }
        Ticket saved = ticketRepository.save(ticket);
        createContributorIfMissing(saved, assignedTo.trim());
        publishEvent("ticket.assigned", saved, "Assigned to " + assignedTo);
        return saved;
    }

    @Override
    @Transactional
    public Ticket updateStatus(UUID ticketId, String status) {
        if (!StringUtils.hasText(status)) {
            throw new ValidationException("status must not be empty");
        }
        Ticket ticket = findExistingTicket(ticketId);
        String normalizedStatus = normalizeEnumValue(status, Status.class);
        Status currentStatus = Status.valueOf(ticket.getStatus());
        Status targetStatus = Status.valueOf(normalizedStatus);

        if (currentStatus == targetStatus) {
            return ticket;
        }

        if (!VALID_STATUS_TRANSITIONS.getOrDefault(currentStatus, Set.of()).contains(targetStatus)) {
            throw new ValidationException(String.format(
                    "Invalid status transition from %s to %s",
                    currentStatus, targetStatus
            ));
        }

        ticket.setStatus(normalizedStatus);
        if (targetStatus == Status.RESOLVED) {
            ticket.setResolvedAt(LocalDateTime.now());
        }
        Ticket saved = ticketRepository.save(ticket);
        if (Status.CLOSED.name().equals(normalizedStatus)) {
            publishEvent("ticket.closed", saved, "Ticket closed");
        } else {
            publishEvent("ticket.updated", saved, "Status updated to " + normalizedStatus);
        }
        return saved;
    }

    @Override
    @Transactional
    public Ticket resolveTicket(UUID ticketId) {
        Ticket ticket = findExistingTicket(ticketId);
        ticket.setStatus(Status.RESOLVED.name());
        ticket.setResolvedAt(LocalDateTime.now());
        Ticket saved = ticketRepository.save(ticket);
        publishEvent("ticket.resolved", saved, "Ticket resolved");
        return saved;
    }

    @Override
    @Transactional
    public Ticket rateTicket(UUID ticketId, Integer rating, String feedback) {
        Ticket ticket = findExistingTicket(ticketId);
        if (rating == null || rating < 1 || rating > 5) {
            throw new ValidationException("rating must be between 1 and 5");
        }
        com.cognizant.Ticket_service.entity.TicketRating ticketRating = new com.cognizant.Ticket_service.entity.TicketRating();
        ticketRating.setTicketId(ticketId);
        ticketRating.setRating(rating);
        ticketRating.setFeedback(StringUtils.hasText(feedback) ? feedback.trim() : null);
        ticketRating.setCreatedAt(LocalDateTime.now());
        ratingRepository.save(ticketRating);
        publishEvent("ticket.rated", ticket, "Rating submitted");
        return ticket;
    }

    @Override
    public List<Ticket> searchTickets(String query) {
        if (!StringUtils.hasText(query)) {
            return getAllTickets();
        }
        String normalizedQuery = query.trim().toLowerCase(Locale.ROOT);
        return ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getDeleted() == null || !ticket.getDeleted())
                .filter(ticket -> matchesSearch(ticket, normalizedQuery))
                .collect(Collectors.toList());
    }

    @Override
    public List<Ticket> searchTickets(String title,
                                      Long categoryId,
                                      String difficultyLevel,
                                      String status,
                                      String assignedTo) {
        return ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getDeleted() == null || !ticket.getDeleted())
                .filter(ticket -> !StringUtils.hasText(title) || containsIgnoreCase(ticket.getTitle(), title))
                .filter(ticket -> categoryId == null || categoryId.equals(ticket.getCategoryId()))
                .filter(ticket -> !StringUtils.hasText(difficultyLevel) || difficultyLevel.equalsIgnoreCase(ticket.getDifficultyLevel()))
                .filter(ticket -> !StringUtils.hasText(status) || status.equalsIgnoreCase(ticket.getStatus()))
                .filter(ticket -> !StringUtils.hasText(assignedTo) || containsIgnoreCase(ticket.getAssignedTo(), assignedTo))
                .collect(Collectors.toList());
    }

    @Override
    public TicketStatisticsDTO getTicketStatistics() {
        List<Ticket> tickets = ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getDeleted() == null || !ticket.getDeleted())
                .collect(Collectors.toList());

        long totalTickets = tickets.size();
        long openTickets = tickets.stream()
                .filter(ticket -> Status.OPEN.name().equalsIgnoreCase(ticket.getStatus()))
                .count();
        long resolvedTickets = tickets.stream()
                .filter(ticket -> Status.RESOLVED.name().equalsIgnoreCase(ticket.getStatus()))
                .count();

        double averageRating = ratingRepository.findAll().stream()
                .mapToInt(rating -> rating.getRating() == null ? 0 : rating.getRating())
                .filter(value -> value > 0)
                .average()
                .orElse(0.0);

        return new TicketStatisticsDTO(totalTickets, openTickets, resolvedTickets, averageRating);
    }

    private Ticket findExistingTicket(UUID ticketId) {
        return ticketRepository.findById(ticketId)
                .filter(ticket -> ticket.getDeleted() == null || !ticket.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketId));
    }

    private void validateTicketRequest(TicketRequestDTO request) {
        if (request == null) {
            throw new ValidationException("Ticket request must not be null");
        }
        if (!StringUtils.hasText(request.getTitle())) {
            throw new ValidationException("title must not be empty");
        }
        if (!StringUtils.hasText(request.getDescription())) {
            throw new ValidationException("description must not be empty");
        }
        if (request.getCategoryId() == null) {
            throw new ValidationException("categoryId must not be null");
        }
        validateEnumValue(request.getDifficultyLevel(), DifficultyLevel.class);
        validateEnumValue(request.getPriority(), Priority.class);
        validateEnumValue(request.getVisibility(), com.cognizant.Ticket_service.entity.Visibility.class);
    }

    private <T extends Enum<T>> String normalizeEnumValue(String value, Class<T> enumClass) {
        if (!StringUtils.hasText(value)) {
            throw new ValidationException(enumClass.getSimpleName() + " value must not be empty");
        }
        try {
            return Enum.valueOf(enumClass, value.trim().toUpperCase(Locale.ROOT)).name();
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Invalid " + enumClass.getSimpleName() + ": " + value);
        }
    }

    private void validateEnumValue(String value, Class<? extends Enum<?>> enumClass) {
        if (!StringUtils.hasText(value)) {
            throw new ValidationException(enumClass.getSimpleName() + " value must not be empty");
        }
        try {
            Enum.valueOf((Class) enumClass, value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Invalid " + enumClass.getSimpleName() + ": " + value);
        }
    }

    /**
     * Returns the email of the currently-authenticated principal (set by
     * {@code JwtAuthenticationFilter} from the JWT's {@code sub} claim), or
     * {@code "system"} for unauthenticated calls (e.g. internal endpoints).
     */
    private String currentPrincipalName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || "anonymousUser".equals(auth.getPrincipal())) {
            return "system";
        }
        return auth.getName();
    }

    private boolean matchesSearch(Ticket ticket, String query) {
        return containsIgnoreCase(ticket.getTitle(), query)
                || containsIgnoreCase(ticket.getDescription(), query)
                || containsIgnoreCase(ticket.getStatus(), query)
                || containsIgnoreCase(ticket.getPriority(), query)
                || containsIgnoreCase(ticket.getAssignedTo(), query);
    }

    private boolean containsIgnoreCase(String value, String query) {
        return StringUtils.hasText(value) && value.toLowerCase(Locale.ROOT).contains(query);
    }

    private void createContributorIfMissing(Ticket ticket, String assignedTo) {
        UUID contributorUserId = parseNullableUuid(assignedTo);
        if (contributorUserId == null) {
            return;
        }
        Optional<TicketContributor> existing = contributorRepository.findByTicketIdAndUserId(ticket.getTicketId(), contributorUserId);
        if (existing.isEmpty()) {
            TicketContributor contributor = new TicketContributor();
            contributor.setTicketId(ticket.getTicketId());
            contributor.setUserId(contributorUserId);
            contributor.setJoinedAt(LocalDateTime.now());
            contributorRepository.save(contributor);
        }
    }

    private UUID parseNullableUuid(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return UUID.fromString(value.trim());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private void publishEvent(String eventType, Ticket ticket, String details) {
        if (ticket == null || ticket.getTicketId() == null) {
            return;
        }

        String categoryName = "";
        if (ticket.getCategoryId() != null) {
            categoryName = categoryRepository.findById(ticket.getCategoryId())
                    .map(Category::getCategoryName)
                    .orElse("");
        }

        List<String> contributors = contributorRepository.findByTicketId(ticket.getTicketId()).stream()
                .map(contributor -> contributor.getUserId() == null ? "" : contributor.getUserId().toString())
                .collect(Collectors.toList());

        String safeDetails = escapeJson(details);
        String safeCategory = escapeJson(categoryName);
        String safeDifficulty = escapeJson(ticket.getDifficultyLevel());
        String contributorArray = contributors.stream()
                .map(id -> String.format("\"%s\"", escapeJson(id)))
                .collect(Collectors.joining(","));

        String payload = String.format(
                "{\"eventType\":\"%s\",\"ticketId\":\"%s\",\"category\":\"%s\",\"difficultyLevel\":\"%s\",\"contributors\":[%s],\"details\":\"%s\"}",
                eventType,
                ticket.getTicketId(),
                safeCategory,
                safeDifficulty,
                contributorArray,
                safeDetails
        );
        kafkaTemplate.send(EVENT_TOPIC, payload);
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
