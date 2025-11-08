package com.example.deviceservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Jws<Claims> jws = jwtService.parse(token);
                Claims claims = jws.getBody();
                String email = claims.getSubject();
                String role = claims.get("role", String.class);
                String aid = claims.get("aid", String.class);
                if (email != null && role != null && aid != null) {
                    UUID authUserId = UUID.fromString(aid);
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserPrincipal principal = new UserPrincipal(authUserId, email, role);
                        var authentication = new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                List.copyOf(principal.getAuthorities())
                        );
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (IllegalArgumentException | JwtException ex) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
