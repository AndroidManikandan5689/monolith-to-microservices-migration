I have successfully completed the integration of Redis Caching and Apache Kafka Message Broker into the User and Order REST API.

Here is a summary of what has been accomplished:

🛠️ What We Did
Maven Dependencies (pom.xml):

Added spring-boot-starter-data-redis to enable caching support.
Added spring-kafka to enable event publishing and message consumption.
Configurations (application.yml & docker-compose.yml):

Configured dynamic Redis connection hosts and enabled Redis Cache managers.
Configured Kafka Broker properties with JSON serializer/deserializer settings.
Added redis (v7 alpine) and kafka (Confluent v7.6 in KRaft mode, Zookeeperless) services to the multi-container compose stack.
Redis Caching:

Created 

CacheConfig.java
 with @EnableCaching and configured customized JSON serialization for cache values (instead of default binary), making caching key entries readable in Redis CLI.
Applied @Cacheable to getUserById and @CacheEvict to updateUser and deleteUser in 

UserServiceImpl.java
.
Apache Kafka Event Broker:

Created 

KafkaConfig.java
 to dynamically provision the order-events topic with 3 partitions (enabling concurrent horizontal scaling for consumers).
Created 

OrderEvent.java
 representing the payload event.
Built 

KafkaProducerService.java
 that sends messages using the orderId as a partition key (guaranteeing in-order processing per partition) and logging success callbacks.
Built 

KafkaConsumerService.java
 implementing @KafkaListener to consume and log events asynchronously in a separate thread pool.
Integrated the producer call inside createOrder in 

OrderServiceImpl.java
.
Test Isolation:

Updated 

UserIntegrationTest.java
 to mock the Kafka services and disable caching during integration tests. This allows tests to run smoothly without requiring active Kafka/Redis containers.
🏃 How to Run and Verify the Changes
Start the upgraded container stack:

bash
cd user-order-api
docker compose up -d --build
Stream application logs to observe transactions:

bash
docker compose logs -f api
Verify Caching & Events:

Refer to the step-by-step verification checklist in the updated 
walkthrough.md
 to inspect cache hit logs and stream Kafka publisher/listener logs as you fire CRUD requests!