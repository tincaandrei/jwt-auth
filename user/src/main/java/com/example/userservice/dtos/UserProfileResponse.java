package com.example.userservice.dtos;

import java.time.Instant;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        UUID authUserId,
        String email,
        String firstName,
        String lastName,
        String phone,
        String address,
        Instant createdAt,
        Instant updatedAt
) {
}
