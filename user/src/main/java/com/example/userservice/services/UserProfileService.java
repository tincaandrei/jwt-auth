package com.example.userservice.services;

import com.example.userservice.dtos.UserProfileRequest;
import com.example.userservice.dtos.UserProfileResponse;
import com.example.userservice.entities.UserProfile;
import com.example.userservice.handlers.exceptions.ResourceNotFoundException;
import com.example.userservice.repositories.UserProfileRepository;
import com.example.userservice.security.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileService {

    private final UserProfileRepository repository;

    public UserProfileResponse upsertForCurrentUser(UserPrincipal principal, UserProfileRequest request) {
        UserPrincipal current = requirePrincipal(principal);
        UserProfile profile = repository.findByAuthUserId(current.authUserId())
                .orElseGet(UserProfile::new);

        if (profile.getId() == null) {
            profile.setAuthUserId(current.authUserId());
        }

        profile.setEmail(current.email());
        profile.setFirstName(request.firstName());
        profile.setLastName(request.lastName());
        profile.setPhone(request.phone());
        profile.setAddress(request.address());

        UserProfile saved = repository.save(profile);
        return toResponse(saved);
    }

    public UserProfileResponse getCurrentUserProfile(UserPrincipal principal) {
        UserPrincipal current = requirePrincipal(principal);
        UserProfile profile = repository.findByAuthUserId(current.authUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found"));
        return toResponse(profile);
    }

    public List<UserProfileResponse> getAllProfiles() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public UserProfileResponse getProfile(UUID id) {
        UserProfile profile = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found"));
        return toResponse(profile);
    }

    public void deleteCurrentUserProfile(UserPrincipal principal) {
        UserPrincipal current = requirePrincipal(principal);
        if (!repository.existsByAuthUserId(current.authUserId())) {
            throw new ResourceNotFoundException("User profile not found");
        }
        repository.deleteByAuthUserId(current.authUserId());
    }

    public void deleteProfile(UUID id) {
        UserProfile profile = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found"));
        repository.delete(profile);
    }

    private UserProfileResponse toResponse(UserProfile profile) {
        return new UserProfileResponse(
                profile.getId(),
                profile.getAuthUserId(),
                profile.getEmail(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getPhone(),
                profile.getAddress(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }

    private UserPrincipal requirePrincipal(UserPrincipal principal) {
        if (principal == null) {
            throw new IllegalArgumentException("Authenticated user required");
        }
        return principal;
    }
}
