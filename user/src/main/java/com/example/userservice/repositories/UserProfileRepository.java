package com.example.userservice.repositories;

import com.example.userservice.entities.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    Optional<UserProfile> findByAuthUserId(UUID authUserId);
    boolean existsByAuthUserId(UUID authUserId);
    void deleteByAuthUserId(UUID authUserId);
}
