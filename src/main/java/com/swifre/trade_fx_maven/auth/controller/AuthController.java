package com.swifre.trade_fx_maven.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swifre.trade_fx_maven.auth.dto.AuthRequest;
import com.swifre.trade_fx_maven.auth.dto.AuthResponse;
import com.swifre.trade_fx_maven.auth.service.JwtService;
import com.swifre.trade_fx_maven.user.entity.User;
import com.swifre.trade_fx_maven.user.service.UserService;
import com.swifre.trade_fx_maven.auth.service.AuthService;

/**
 * REST controller for handling user authentication (login).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final UserService userService; // To get UserDetails after authentication

    @Autowired
    public AuthController(AuthService authService, JwtService jwtService, UserService userService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    /**
     * Handles user login and generates a JWT upon successful authentication.
     * 
     * @param authRequest The authentication request containing username and
     *                    password.
     * @return ResponseEntity with JWT token if successful, or unauthorized status.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest authRequest) {
        // Authenticate the user using Spring Security's AuthService
        try {
            User user = this.authService.validateUser(
                    authRequest.getEmail(),
                    authRequest.getPassword());

            // If authentication is successful, generate JWT
            if (user != null) {
                String jwt = this.jwtService.generateToken(user); // Generate token
                return new ResponseEntity<>(new AuthResponse(jwt, user), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Example: Endpoint to register a new user.
     * For a full application, this would have more robust validation and error
     * handling.
     * This endpoint does NOT require authentication.
     * 
     * @param user The user object to register.
     * @return ResponseEntity with the created user.
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        // Here, the password will be encoded by UserService before saving.
        User createdUser = userService.createUser(user);
        // Optionally generate a token for the newly registered user if they should be
        // logged in immediately
        // String jwt = jwtService.generateToken(createdUser);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
}