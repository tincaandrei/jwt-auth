package com.example.userservice.security;

import java.util.UUID;

public record UserPrincipal(UUID authUserId, String email, String role) {
}
