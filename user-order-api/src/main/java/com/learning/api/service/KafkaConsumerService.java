package com.learning.api.service;

import com.learning.api.dto.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * ============================================================
 * INTERVIEW CONCEPT: Kafka Consumers & Consumer Groups
 * ============================================================
 *
 * @KafkaListener
 * - Registers a message listener container matching the specified topic.
 * - Runs asynchronously in a dedicated Spring-managed thread.
 *
 * Consumer Groups (groupId):
 * - Identifies the group of consumers that cooperate to consume events.
 * - Each message sent to the topic is delivered to exactly one consumer instance
 *   in this group (load-balanced across active consumers).
 */
@Service
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);

    @KafkaListener(topics = "order-events", groupId = "user-order-group")
    public void consumeOrderEvent(OrderEvent event) {
        log.info(">>> [KAFKA CONSUMER] Message successfully consumed from 'order-events':");
        log.info("    -> Order ID:      {}", event.getOrderId());
        log.info("    -> Product Name:   {}", event.getProductName());
        log.info("    -> Quantity:       {}", event.getQuantity());
        log.info("    -> Customer Email: {}", event.getUserEmail());
        log.info("    -> Purchased At:   {}", event.getTimestamp());
        
        // This is where you would call an email service or update analytics.
    }
}
