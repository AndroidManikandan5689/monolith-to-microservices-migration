package com.learning.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * ============================================================
 * INTERVIEW CONCEPT: Data Transfer Object (DTO) & Validation
 * ============================================================
 *
 * DTOs carry data between the controller layer and the service layer.
 *
 * Validation Annotations:
 * - @NotBlank: String is not null and has non-whitespace characters.
 * - @Email: Validates proper email format.
 * - @Size: Constraints the characters count.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
}
