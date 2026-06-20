package com.learning.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * ============================================================
 * INTERVIEW CONCEPT: JPA Relationships
 * ============================================================
 *
 * @ManyToOne
 * - Indicates many orders belong to one user.
 * - By default, @ManyToOne uses EAGER fetching. We override it to LAZY
 *   to optimize SQL query execution.
 *
 * @JoinColumn(name = "user_id", nullable = false)
 * - Defines the foreign key column in the "orders" table.
 * - nullable = false ensures an order cannot exist without a valid user.
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
