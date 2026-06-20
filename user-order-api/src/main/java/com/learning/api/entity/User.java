package com.learning.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * INTERVIEW CONCEPT: Bidirectional Relationships
 * ============================================================
 *
 * @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
 * - mappedBy = "user" indicates the "user" field in the Order entity owns the relationship.
 * - cascade = CascadeType.ALL ensures saving/deleting a User applies to all their Orders.
 * - orphanRemoval = true ensures that if an Order is removed from the list, it is deleted from the DB.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 100)
    private String password; // Will hold BCrypt hash

    @Column(nullable = false, length = 20)
    private String role; // e.g. "ROLE_USER", "ROLE_ADMIN"

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();
}
