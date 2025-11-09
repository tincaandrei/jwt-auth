package com.energy.auth.dto;

import com.energy.auth.entity.Role;

import java.time.Instant;
import java.util.UUID;

public class UserResponse {
    private UUID id;
    private String username;
    private Role role;
    private Instant createdAt;

    public UserResponse(UUID id, String username, Role role, Instant createdAt) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
