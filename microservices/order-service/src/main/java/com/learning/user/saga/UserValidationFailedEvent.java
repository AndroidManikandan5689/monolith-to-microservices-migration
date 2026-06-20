package com.learning.user.saga;

import lombok.*;

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
