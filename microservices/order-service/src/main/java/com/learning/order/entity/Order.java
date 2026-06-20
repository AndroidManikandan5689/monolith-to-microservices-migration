package com.learning.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    // Database Decoupling: reference the User by its ID directly
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // SAGA Transaction State
    @Column(nullable = false, length = 20)
    private String status; // PENDING, CONFIRMED, CANCELLED
}
