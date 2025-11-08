package com.example.userservice.controllers;

import com.example.userservice.dtos.UserProfileRequest;
import com.example.userservice.dtos.UserProfileResponse;
import com.example.userservice.security.UserPrincipal;
import com.example.userservice.services.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/me")
    public UserProfileResponse getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        return userProfileService.getCurrentUserProfile(principal);
    }

    @PutMapping("/me")
    public UserProfileResponse upsertCurrentUser(@AuthenticationPrincipal UserPrincipal principal,
                                                 @Valid @RequestBody UserProfileRequest request) {
        return userProfileService.upsertForCurrentUser(principal, request);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        userProfileService.deleteCurrentUserProfile(principal);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<UserProfileResponse> getAll() {
        return userProfileService.getAllProfiles();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public UserProfileResponse getById(@PathVariable UUID id) {
        return userProfileService.getProfile(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable UUID id) {
        userProfileService.deleteProfile(id);
    }
}
