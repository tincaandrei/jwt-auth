package com.energy.user.dto;

import com.energy.user.entity.Role;

import java.util.UUID;

public class UserDto {
    private UUID id;
    private String username;
    private Role role;

    public UserDto(UUID id, String username, Role role) {
        this.id = id;
        this.username = username;
        this.role = role;
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
}
