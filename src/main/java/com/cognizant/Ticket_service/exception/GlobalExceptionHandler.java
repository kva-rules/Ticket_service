package com.cognizant.Ticket_service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Centralised exception → HTTP status mapping for the ticket service.
 *
 * <p>Without this advice, unhandled domain exceptions bubble through the Spring
 * Security filter chain and the {@code AuthenticationEntryPoint} returns a misleading
 * 401 — the controller actually authenticated fine, but the body validation failed.
 * That obscures real bugs (and makes API consumers think their token is bad).
 *
 * <p>Mapping:
 * <ul>
 *   <li>{@link ValidationException}        → 400 Bad Request</li>
 *   <li>{@link ResourceNotFoundException}  → 404 Not Found</li>
 *   <li>any other {@link RuntimeException} → 500 with a stable JSON envelope</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(ValidationException ex) {
        log.warn("Validation failure: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(envelope("VALIDATION_ERROR", ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        log.warn("Resource missing: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(envelope("NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        // Don't shadow Spring Security's own AccessDeniedException / AuthenticationException —
        // those have their own handler chain. Anything else gets a clean 500 envelope so the
        // client doesn't see HTML stack traces and we don't accidentally return 401.
        if (ex.getClass().getName().startsWith("org.springframework.security")) {
            throw ex;
        }
        log.error("Unhandled error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(envelope("INTERNAL_ERROR", ex.getMessage()));
    }

    private static Map<String, Object> envelope(String code, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("errorCode", code);
        body.put("message", message);
        body.put("timestamp", OffsetDateTime.now().toString());
        return body;
    }
}
