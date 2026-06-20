package com.learning.order.saga;

import com.learning.order.entity.Order;
import com.learning.order.repository.OrderRepository;
import com.learning.user.saga.UserValidatedEvent;
import com.learning.user.saga.UserValidationFailedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ============================================================
 * INTERVIEW CONCEPT: SAGA Choreography (Orchestrator role)
 * ============================================================
 *
 * OrderSagaOrchestrator:
 * - Listens to validation response callbacks from user-service.
 * - On Success (UserValidated): updates order status to CONFIRMED.
 * - On Failure (UserValidationFailed): triggers a Compensating Transaction
 *   by updating the order status to CANCELLED.
 */
@Service
@Transactional
public class OrderSagaOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(OrderSagaOrchestrator.class);

    private final OrderRepository orderRepository;

    public OrderSagaOrchestrator(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @KafkaListener(topics = "user-validation-events", groupId = "order-service-group")
    public void handleUserValidatedEvent(UserValidatedEvent event) {
        log.info(">>> [SAGA ORCHESTRATOR] Received UserValidatedEvent: {}", event);

        Order order = orderRepository.findById(event.getOrderId()).orElse(null);
        if (order != null && "PENDING".equals(order.getStatus())) {
            order.setStatus("CONFIRMED");
            orderRepository.save(order);
            log.info(">>> [SAGA ORCHESTRATOR] Order {} has been successfully CONFIRMED.", order.getId());
        }
    }

    @KafkaListener(topics = "user-validation-events", groupId = "order-service-group")
    public void handleUserValidationFailedEvent(UserValidationFailedEvent event) {
        log.warn(">>> [SAGA ORCHESTRATOR] Received UserValidationFailedEvent: {}", event);

        Order order = orderRepository.findById(event.getOrderId()).orElse(null);
        if (order != null && "PENDING".equals(order.getStatus())) {
            // COMPENSATING TRANSACTION: Rollback transaction state to CANCELLED
            order.setStatus("CANCELLED");
            orderRepository.save(order);
            log.warn(">>> [SAGA ORCHESTRATOR] Compensating Transaction Executed. Order {} status set to CANCELLED.", order.getId());
        }
    }
}
