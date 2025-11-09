package com.energy.user.dto;

import com.energy.user.entity.Role;

public class UpdateUserRequest {
    private Role role;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
