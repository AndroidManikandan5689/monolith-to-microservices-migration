package com.learning.order.saga;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrderCreatedEvent {
    private Long orderId;
    private Long userId;
    private String productName;
    private Integer quantity;
}
