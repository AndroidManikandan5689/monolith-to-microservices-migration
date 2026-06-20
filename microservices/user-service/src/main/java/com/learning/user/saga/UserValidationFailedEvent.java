package com.learning.user.saga;

import lombok.*;

/**
 * Event published by user-service when a user check fails (non-existent user).
 * This will trigger the compensating rollback in order-service.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserValidationFailedEvent {
    private Long orderId;
    private Long userId;
    private String reason;
}
