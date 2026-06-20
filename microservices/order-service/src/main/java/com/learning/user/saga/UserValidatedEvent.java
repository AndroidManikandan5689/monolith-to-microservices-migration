package com.learning.user.saga;

import lombok.*;

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
