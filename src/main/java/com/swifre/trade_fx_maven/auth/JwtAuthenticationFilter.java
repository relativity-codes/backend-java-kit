package com.swifre.trade_fx_maven.auth;

import java.io.IOException;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.swifre.trade_fx_maven.auth.service.AuthService;
import com.swifre.trade_fx_maven.auth.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.swifre.trade_fx_maven.user.entity.User;

/**
 * Custom Spring Security filter to intercept incoming requests and validate
 * JWTs.
 * This filter runs once per request.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AuthService authService;

    /**
     * Constructor for JwtAuthenticationFilter.
     * 
     * @param jwtService  Service for JWT operations.
     * @param authService Service to load user details.
     */
    public JwtAuthenticationFilter(JwtService jwtService, AuthService authService) {
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization"); // Get Authorization header
        final String jwt;
        final UUID id;

        // 1. Check if Authorization header exists and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Continue to next filter
            return;
        }

        // 2. Extract JWT token
        jwt = authHeader.substring(7); // "Bearer ".length() is 7

        // 3. Extract username from JWT
        id = jwtService.extractId(jwt);

        // 4. Validate JWT and set up SecurityContext
        if (id != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // If id is present and no authentication is currently set in
            // SecurityContext
            User user = this.authService.getUserById(id).orElse(null);

            if (user != null && jwtService.isTokenValid(jwt, user)) {
                // If token is valid, create an authentication object
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user,
                        null, // credentials are null as we're authenticated by token
                        user.getAuthorities() // user's authorities/roles
                );
                // Set details from the request
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                // Update SecurityContextHolder
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 5. Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}