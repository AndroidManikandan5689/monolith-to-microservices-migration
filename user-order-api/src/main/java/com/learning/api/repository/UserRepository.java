package com.learning.api.repository;

import com.learning.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ============================================================
 * INTERVIEW CONCEPT: Resolving N+1 using JOIN FETCH
 * ============================================================
 *
 * "LEFT JOIN FETCH u.orders" tells Hibernate to fetch both User rows
 * and their associated Orders collection in a single SELECT query using an SQL JOIN.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Optimized finder using JPQL JOIN FETCH
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.orders")
    List<User> findAllWithOrders();

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
