package com.learning.user.saga;

import com.learning.order.saga.OrderCreatedEvent;
import com.learning.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * ============================================================
 * INTERVIEW CONCEPT: SAGA Choreography (Participant role)
 * ============================================================
 *
 * UserSagaHandler:
 * - Listens to OrderCreatedEvents published by order-service.
 * - Executes local database verification (checking if User exists).
 * - Publishes success or failure events to coordinate the transaction outcome.
 */
@Service
public class UserSagaHandler {

    private static final Logger log = LoggerFactory.getLogger(UserSagaHandler.class);
    private static final String TOPIC = "user-validation-events";

    private final UserRepository userRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public UserSagaHandler(UserRepository userRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "order-events", groupId = "user-service-group")
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        log.info(">>> [SAGA PARTICIPANT] Received OrderCreatedEvent: {}", event);

        boolean userExists = userRepository.existsById(event.getUserId());

        if (userExists) {
            log.info(">>> [SAGA PARTICIPANT] Validation Success. User {} exists. Emitting UserValidatedEvent...", event.getUserId());
            UserValidatedEvent successEvent = UserValidatedEvent.builder()
                    .orderId(event.getOrderId())
                    .userId(event.getUserId())
                    .build();
            
            kafkaTemplate.send(TOPIC, event.getOrderId().toString(), successEvent);
        } else {
            log.warn(">>> [SAGA PARTICIPANT] Validation Failed. User {} does not exist. Emitting UserValidationFailedEvent...", event.getUserId());
            UserValidationFailedEvent failEvent = UserValidationFailedEvent.builder()
                    .orderId(event.getOrderId())
                    .userId(event.getUserId())
                    .reason("User does not exist in User Database.")
                    .build();

            kafkaTemplate.send(TOPIC, event.getOrderId().toString(), failEvent);
        }
    }
}
