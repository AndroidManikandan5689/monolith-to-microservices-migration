package com.learning.user.service;

import com.learning.user.dto.UserDto;
import org.springframework.data.domain.Page;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto getUserById(Long id);
    Page<UserDto> getAllUsers(int page, int size, String sortBy, String sortDir);
    UserDto updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
}
