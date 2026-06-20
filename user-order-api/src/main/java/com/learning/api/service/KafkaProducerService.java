package com.learning.api.service;

import com.learning.api.dto.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * ============================================================
 * INTERVIEW CONCEPT: Kafka Producers & Message Keys
 * ============================================================
 *
 * KafkaTemplate:
 * - Spring's high-level abstraction wrapping a Kafka Producer client.
 *
 * Message Keys:
 * - Sending messages with a KEY (e.g. orderId string) guarantees that
 *   all events for that specific order are routed to the same partition.
 * - This ensures ordering guarantees (messages processed sequentially in FIFO order).
 */
@Service
public class KafkaProducerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);
    private static final String TOPIC = "order-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderEvent(OrderEvent event) {
        log.info(">>> [KAFKA PRODUCER] Publishing event to topic '{}': {}", TOPIC, event);
        
        // Using orderId string as the partition key
        String partitionKey = event.getOrderId().toString();
        
        kafkaTemplate.send(TOPIC, partitionKey, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info(">>> [KAFKA PRODUCER] Message successfully published. Partition: {}, Offset: {}",
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error(">>> [KAFKA PRODUCER] Failed to publish message: {}", ex.getMessage());
                    }
                });
    }
}
