package com.example.userservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserProfileRequest(
        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name must be at most 100 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name must be at most 100 characters")
        String lastName,

        @Size(max = 20, message = "Phone number must be at most 20 characters")
        String phone,

        @Size(max = 255, message = "Address must be at most 255 characters")
        String address
) {
}
