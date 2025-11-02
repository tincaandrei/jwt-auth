// src/main/java/com/example/demo/entities/RefreshToken.java
package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_token",
        indexes = {
                @Index(name = "idx_rt_user", columnList = "user_id"),
                @Index(name = "idx_rt_expires", columnList = "expiresAt"),
                @Index(name = "idx_rt_revoked", columnList = "revoked")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // Store SHA-256 of the refresh token, not the raw value
    @Column(nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(nullable = false)
    private Instant issuedAt;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked = false;

    private Instant revokedAt;
}
