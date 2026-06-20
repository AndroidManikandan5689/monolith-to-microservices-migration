package com.learning.api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * ============================================================
 * INTERVIEW CONCEPT: Standard API Error Design
 * ============================================================
 *
 * A unified error schema makes it easy for frontend clients (Web, Mobile)
 * to handle errors consistently.
 *
 * @JsonInclude(JsonInclude.Include.NON_NULL)
 * - Tells Jackson mapper NOT to include fields that are null in the output JSON.
 * - This prevents showing "errors: null" for standard 404 or 500 errors.
 */
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private Map<String, String> errors; // Holds validation field errors
}
