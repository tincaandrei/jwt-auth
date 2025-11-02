package com.example.demo.services;

import com.example.demo.dtos.LoginRequest;
import com.example.demo.dtos.RegisterRequest;
import com.example.demo.dtos.TokenResponse;
import com.example.demo.dtos.UserResponse;
import com.example.demo.entities.Role;
import com.example.demo.entities.RoleName;
import com.example.demo.entities.User;
import com.example.demo.repositories.RoleRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.refresh-ttl-days}")
    private long refreshTtlDays;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("Email already exists");
        }

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("ROLE_USER not seeded"));

        User user = User.builder()
                .username(request.getUsername())
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .build();
        User saved = userRepository.save(user);
        return new UserResponse(saved.getId(), saved.getEmail(), saved.getRole().getName().name());
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().toLowerCase(Locale.ROOT);
        User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String accessToken = jwtService.generateAccess(user.getId(), user.getEmail(), user.getRole().getName().name());
        String refreshToken = refreshTokenService.issue(user, refreshTtlDays);
        return new TokenResponse(accessToken, refreshToken);
    }

    @Transactional
    public TokenResponse refresh(String rawRefreshToken) {
        User user = refreshTokenService.validateAndGetUser(rawRefreshToken);
        refreshTokenService.revoke(rawRefreshToken);
        String newRefreshToken = refreshTokenService.issue(user, refreshTtlDays);
        String accessToken = jwtService.generateAccess(user.getId(), user.getEmail(), user.getRole().getName().name());
        return new TokenResponse(accessToken, newRefreshToken);
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        refreshTokenService.revoke(rawRefreshToken);
    }

    @Transactional
    public void revokeAll(User user) {
        refreshTokenService.revokeAllForUser(user);
    }
}
