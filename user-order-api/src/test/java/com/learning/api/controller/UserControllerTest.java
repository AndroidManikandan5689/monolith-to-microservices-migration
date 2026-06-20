package com.learning.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.api.config.SecurityConfig;
import com.learning.api.dto.UserDto;
import com.learning.api.security.JwtTokenProvider;
import com.learning.api.security.UserDetailsServiceImpl;
import com.learning.api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ============================================================
 * INTERVIEW CONCEPT: Web MVC Slice Testing
 * ============================================================
 *
 * @WebMvcTest(UserController.class)
 * - Restricts application context loading only to the UserController.
 *
 * @MockBean
 * - Adds Mockito mocks to the Spring ApplicationContext.
 *
 * @WithMockUser
 * - Tells Spring Security to execute this test with a mock authenticated user,
 *   bypassing the need for generating authorization headers.
 */
@WebMvcTest(UserController.class)
@Import(SecurityConfig.class) // Import security config rules
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    // Spring Security configuration beans that must be mocked during context loading
    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper; // Serializes objects to JSON

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .name("Alex")
                .email("alex@example.com")
                .build();
    }

    @Test
    @WithMockUser // Simulates authenticated user session
    void createUser_Success() throws Exception {
        // Arrange
        when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

        // Act & Assert
        mockMvc.perform(post("/users")
                        .with(csrf()) // Attaches CSRF token mock if validation requires it
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Alex"))
                .andExpect(jsonPath("$.email").value("alex@example.com"));
    }

    @Test
    @WithMockUser
    void createUser_ValidationFailure_Returns400() throws Exception {
        // Create an invalid payload (blank name)
        UserDto invalidDto = UserDto.builder()
                .name("")
                .email("invalid-email")
                .build();

        // Act & Assert
        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.email").exists());
    }
}
