package com.example.demo.dtos.builders;

import com.example.demo.dtos.UserDTO;
import com.example.demo.entities.User;

public final class UserBuilder {
    private UserBuilder() {
    }

    public static UserDTO toUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole() != null ? user.getRole().getName().name() : null
        );
    }
}
