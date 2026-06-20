package com.learning.api.repository;

import com.learning.api.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Find all orders belonging to a specific user
    List<Order> findByUserId(Long userId);
}
