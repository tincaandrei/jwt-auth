package com.energy.auth.service;

import com.energy.auth.dto.AuthResponse;
import com.energy.auth.dto.LoginRequest;
import com.energy.auth.dto.RegisterRequest;
import com.energy.auth.dto.UserResponse;
import com.energy.auth.entity.Role;
import com.energy.auth.entity.User;
import com.energy.auth.repository.UserRepository;
import com.energy.auth.security.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        Role desiredRole = Optional.ofNullable(request.getRole()).orElse(Role.CLIENT);
        boolean hasAnyUser = userRepository.count() > 0;
        if (desiredRole == Role.ADMIN && hasAnyUser && !currentUserIsAdmin()) {
            desiredRole = Role.CLIENT;
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(desiredRole);
        user.setCreatedAt(Instant.now());
        User saved = userRepository.save(user);
        return new UserResponse(saved.getId(), saved.getUsername(), saved.getRole(), saved.getCreatedAt());
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        String token = jwtService.generateToken(user.getId().toString(), user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getId().toString(), user.getUsername(), user.getRole());
    }

    public UserResponse me(String token) {
        Claims claims = jwtService.extractAllClaims(token);
        String username = claims.get("username", String.class);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return new UserResponse(user.getId(), user.getUsername(), user.getRole(), user.getCreatedAt());
    }

    private boolean currentUserIsAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
