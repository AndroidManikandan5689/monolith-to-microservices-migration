package com.learning.api.service;

import com.learning.api.dto.UserDto;
import com.learning.api.entity.User;
import com.learning.api.exception.EmailAlreadyExistsException;
import com.learning.api.exception.ResourceNotFoundException;
import com.learning.api.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================
 * INTERVIEW CONCEPT: Constructor-based Dependency Injection
 * ============================================================
 *
 * Best Practice: Instead of using @Autowired on the fields, we define
 * the repository dependency as a final variable and let Lombok's
 * @RequiredArgsConstructor generate the constructor. Spring 4.3+ automatically
 * wires constructor arguments if there is only one constructor.
 *
 * Advantages:
 * 1. Enables immutability (final fields).
 * 2. Prevents NullPointerExceptions (dependencies are guaranteed to be injected).
 * 3. Easy to write unit tests (you can just pass a mock repository via constructor).
 */
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        // Validate uniqueness of email
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + userDto.getEmail());
        }

        User user = convertToEntity(userDto);
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    @Override
    @Cacheable(value = "users", key = "#id")
    public UserDto getUserById(Long id) {
        // Logging is added here to demonstrate caching in terminal logs.
        // If cached, this method body is bypassed and no DB query log is printed.
        System.out.println(">>> [DB QUERY] Fetching user " + id + " from database...");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToDto(user);
    }

    @Override
    public Page<UserDto> getAllUsers(int page, int size, String sortBy, String sortDir) {
        // Setup sorting direction
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();

        // Create pageable request parameters
        Pageable pageable = PageRequest.of(page, size, sort);

        // Fetch paginated entities
        Page<User> usersPage = userRepository.findAll(pageable);

        // Map Page<Entity> to Page<DTO>
        return usersPage.map(this::convertToDto);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public UserDto updateUser(Long id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // If email is changing, make sure new email isn't already taken
        if (!existingUser.getEmail().equals(userDto.getEmail()) && userRepository.existsByEmail(userDto.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + userDto.getEmail());
        }

        existingUser.setName(userDto.getName());
        existingUser.setEmail(userDto.getEmail());

        User updatedUser = userRepository.save(existingUser);
        return convertToDto(updatedUser);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    // Helper methods for conversion (Entities <-> DTOs)
    private User convertToEntity(UserDto dto) {
        return User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .password("default_temp_pass_needs_reset") // fallback default for CRUD mapping
                .role("ROLE_USER")
                .build();
    }

    private UserDto convertToDto(User entity) {
        return UserDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .build();
    }
}
