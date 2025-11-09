package com.energy.user.service;

import com.energy.user.dto.CreateUserRequest;
import com.energy.user.dto.UpdateUserRequest;
import com.energy.user.dto.UserDto;
import com.energy.user.entity.Role;
import com.energy.user.entity.User;
import com.energy.user.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserManagementService {

    private final UserRepository userRepository;

    public UserManagementService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(user -> new UserDto(user.getId(), user.getUsername(), user.getRole()))
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserDto findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return new UserDto(user.getId(), user.getUsername(), user.getRole());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserDto create(CreateUserRequest request) {
        userRepository.findByUsername(request.getUsername()).ifPresent(existing -> {
            throw new IllegalArgumentException("Username already exists");
        });
        if (request.getId() != null && userRepository.existsById(request.getId())) {
            throw new IllegalArgumentException("User id already exists");
        }

        User user = new User();
        user.setId(request.getId() != null ? request.getId() : UUID.randomUUID());
        user.setUsername(request.getUsername());
        user.setRole(request.getRole() != null ? request.getRole() : Role.CLIENT);
        User saved = userRepository.save(user);
        return new UserDto(saved.getId(), saved.getUsername(), saved.getRole());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserDto update(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        User saved = userRepository.save(user);
        return new UserDto(saved.getId(), saved.getUsername(), saved.getRole());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(UUID id) {
        userRepository.deleteById(id);
    }

    public UserDto currentUser(String username) {
        return userRepository.findByUsername(username)
                .map(user -> new UserDto(user.getId(), user.getUsername(), user.getRole()))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
