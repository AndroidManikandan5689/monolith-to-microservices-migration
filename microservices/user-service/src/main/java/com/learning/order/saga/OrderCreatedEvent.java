package com.learning.order.saga;

import lombok.*;

import java.math.BigDecimal;

/**
 * Event published by order-service when an order is created (status PENDING).
 */
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
