package com.learning.user.saga;

import lombok.*;

/**
 * Event published by user-service when a user checks out as valid (exists and active).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserValidatedEvent {
    private Long orderId;
    private Long userId;
}
