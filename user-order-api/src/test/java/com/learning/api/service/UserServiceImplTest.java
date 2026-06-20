package com.learning.api.service;

import com.learning.api.dto.UserDto;
import com.learning.api.entity.User;
import com.learning.api.exception.EmailAlreadyExistsException;
import com.learning.api.exception.ResourceNotFoundException;
import com.learning.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ============================================================
 * INTERVIEW CONCEPT: Mockito Unit Testing
 * ============================================================
 *
 * @ExtendWith(MockitoExtension.class)
 * - Tells JUnit 5 to initialize Mockito annotations (@Mock, @InjectMocks).
 *
 * Why Mock?
 * - We test the service logic in complete isolation from the DB.
 * - Tests execute in milliseconds.
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Alex")
                .email("alex@example.com")
                .password("hashed_pass")
                .role("ROLE_USER")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("Alex")
                .email("alex@example.com")
                .build();
    }

    @Test
    void createUser_Success() {
        // Arrange (Stubbing mock behavior)
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserDto result = userService.createUser(userDto);

        // Assert
        assertNotNull(result);
        assertEquals("alex@example.com", result.getEmail());
        assertEquals("Alex", result.getName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_ThrowsEmailAlreadyExistsException() {
        // Arrange
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(userDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        UserDto result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getUserById_ThrowsResourceNotFoundException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
    }
}
