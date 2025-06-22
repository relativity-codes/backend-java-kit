package com.swifre.trade_fx_maven.auth;

import java.io.IOException;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.swifre.trade_fx_maven.auth.service.JwtService;
import com.swifre.trade_fx_maven.user.entity.User;
import com.swifre.trade_fx_maven.user.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Custom Spring Security filter to intercept incoming requests and validate
 * JWTs.
 * This filter runs once per request.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    /**
     * Constructor for JwtAuthenticationFilter.
     * 
     * @param jwtService     Service for JWT operations.
     * @param userRepository Repository to load user details.
     */
    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
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
        id = this.jwtService.extractId(jwt);

        // 4. Validate JWT and set up SecurityContext
        if (id != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // If id is present and no authentication is currently set in
            // SecurityContext
            User user = this.userRepository.findById(id).orElse(null);

            if (user != null && this.jwtService.isTokenValid(jwt, user)) {
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