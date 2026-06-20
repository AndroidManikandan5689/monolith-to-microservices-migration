package com.learning.api.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * ============================================================
 * INTERVIEW CONCEPT: Kafka Topics & Partitioning
 * ============================================================
 *
 * NewTopic Bean:
 * - Spring's KafkaAdmin automatically detects NewTopic beans and
 *   provisions them on the Kafka bootstrap broker.
 *
 * Partitions:
 * - We configure 3 partitions. Partitions are the unit of scalability
 *   in Kafka. They allow multiple consumers in the same consumer group
 *   to process messages concurrently.
 *
 * Replicas:
 * - We configure a replication factor of 1 since we are running a single-node
 *   local broker. In production, use at least 3 replicas for high availability.
 */
@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic orderEventsTopic() {
        return TopicBuilder.name("order-events")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
