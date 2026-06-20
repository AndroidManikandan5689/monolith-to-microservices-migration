package com.learning.api.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO carrying details of the Order event published to Kafka.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrderEvent {
    private Long orderId;
    private String productName;
    private Integer quantity;
    private String userEmail;
    private LocalDateTime timestamp;
}
