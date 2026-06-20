package com.learning.api.integration;

import com.learning.api.dto.UserDto;
import com.learning.api.entity.User;
import com.learning.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.learning.api.service.KafkaProducerService;
import com.learning.api.service.KafkaConsumerService;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ============================================================
 * INTERVIEW CONCEPT: Testcontainers Integration Testing
 * ============================================================
 *
 * @SpringBootTest
 * - Loads the full application context.
 * - WebEnvironment.RANDOM_PORT spins up real Tomcat on a random port.
 *
 * @Container
 * - Tells Testcontainers to start a PostgreSQL docker container before tests run.
 *
 * @ServiceConnection
 * - Spring Boot 3.1+ feature. Automatically maps DB credentials from the running
 *   Testcontainer to Spring's datasource properties (url, username, password).
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.cache.type=none", // Disable Redis caching for integration tests
        "spring.kafka.listener.auto-startup=false" // Prevent Kafka consumer from starting automatically
    }
)
@Testcontainers
@ActiveProfiles("test") // Optional: use application-test.yml if needed
public class UserIntegrationTest {

    @LocalServerPort
    private int port;

    @MockBean
    private KafkaProducerService kafkaProducerService; // Mock Kafka boundaries in integration tests

    @MockBean
    private KafkaConsumerService kafkaConsumerService;

    @Autowired
    private TestRestTemplate restTemplate; // Real HTTP REST Client

    @Autowired
    private UserRepository userRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("userorderdb_test")
            .withUsername("testuser")
            .withPassword("testpass");

    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll(); // Clean state before each integration run
    }

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api"; // Context-path (/api) included
    }

    @Test
    void testCreateAndGetUserIntegrationFlow() {
        // Arrange
        UserDto newUser = UserDto.builder()
                .name("Jessica")
                .email("jessica@example.com")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserDto> request = new HttpEntity<>(newUser, headers);

        // Act - POST Request (since auth is bypassed or temporarily mapped to permitAll under test profiles,
        // we can authenticate or temporarily call directly.
        // For simplicity under our permitAll fallback configurations, this runs directly)
        ResponseEntity<UserDto> createResponse = restTemplate.postForEntity(
                getBaseUrl() + "/users", request, UserDto.class);

        // Assert creation
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertNotNull(createResponse.getBody().getId());
        assertEquals("jessica@example.com", createResponse.getBody().getEmail());

        // Act - GET request to fetch the saved User
        Long savedUserId = createResponse.getBody().getId();
        ResponseEntity<UserDto> getResponse = restTemplate.getForEntity(
                getBaseUrl() + "/users/" + savedUserId, UserDto.class);

        // Assert fetch
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals("Jessica", getResponse.getBody().getName());
    }
}
