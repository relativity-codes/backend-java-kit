package com.swifre.trade_fx_maven.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import com.swifre.trade_fx_maven.auth.dto.AuthRequest;
import com.swifre.trade_fx_maven.auth.dto.AuthResponse;
import com.swifre.trade_fx_maven.auth.dto.ConfirmPasswordResetRequest;
import com.swifre.trade_fx_maven.auth.dto.ResetPasswordRequest;
import com.swifre.trade_fx_maven.auth.dto.UpdateEmailRequest;
import com.swifre.trade_fx_maven.auth.dto.UpdatePasswordRequest;
import com.swifre.trade_fx_maven.auth.service.JwtService;
import com.swifre.trade_fx_maven.user.dtos.UserDto;
import com.swifre.trade_fx_maven.user.entity.User;
import com.swifre.trade_fx_maven.user.enums.UserType;
import com.swifre.trade_fx_maven.user.service.UserService;
import com.swifre.trade_fx_maven.auth.service.AuthService;

/**
 * REST controller for handling user authentication (login).
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Operations related to user authentication")
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
    @Operation(summary = "User Login", description = "Authenticates a user and returns a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials"),
            @ApiResponse(responseCode = "500", description = "Internal server error - Authentication failed")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody AuthRequest authRequest) {
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
    @Operation(summary = "User Registration", description = "Registers a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid user data"),
            @ApiResponse(responseCode = "500", description = "Internal server error - Registration failed")
    })
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody UserDto user) {
        // Here, the password will be encoded by UserService before saving.
        User userEntity = new User(); // Create a new User entity
        userEntity.setUsername(user.getUsername());
        userEntity.setEmail(user.getEmail());
        userEntity.setPassword(user.getPassword());
        UserType userType = UserType.USER;
        userEntity.setUserType(userType);
        User createdUser = this.userService.createUser(userEntity);
        // Optionally generate a token for the newly registered user if they should be
        // logged in immediately
        // String jwt = jwtService.generateToken(createdUser);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Example: Endpoint to verify a user's email.
     * This would typically be called after the user clicks a verification link in
     * an
     * email.
     * 
     * @param token The JWT token containing the user's ID.
     * @return ResponseEntity indicating success or failure.
     */
    @Operation(summary = "Verify Email", description = "Verifies a user's email address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email verified successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid token"),
            @ApiResponse(responseCode = "404", description = "Not Found - User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error - Verification failed")
    })
    @PostMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam("token") String token) {
        try {
            this.authService.verifyEmail(token);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Example: Endpoint to get the current authenticated user's details.
     * This would typically be used to retrieve user information after login.
     * 
     * @return ResponseEntity with the current user's details.
     */
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        User user = this.authService.getCurrentUser();
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Example: Endpoint to update the current user's password. This would typically
     * be used in a profile settings page.
     * 
     * @param updatePasswordRequest The request object containing the old and new
     *                              passwords.
     * @return ResponseEntity indicating success or failure.
     */
    @PostMapping("/me/password")
    public ResponseEntity<String> updatePassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest) {
        try {
            User user = this.authService.getCurrentUser();
            if (user == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            this.authService.changePassword(user.getId(), updatePasswordRequest.getOldPassword(),
                    updatePasswordRequest.getNewPassword());
            return new ResponseEntity<>("Password updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/me/email")
    public ResponseEntity<String> updateEmail(@Valid @RequestBody UpdateEmailRequest updateEmailRequest) {
        try {
            User user = this.authService.getCurrentUser();
            if (user == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            this.authService.updateEmail(user.getId(), updateEmailRequest.getEmail());
            return new ResponseEntity<>("Email updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // reset password request email
    @PostMapping("/reset-password")
    public ResponseEntity<String> requestPasswordReset(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        try {
            this.authService.requestPasswordReset(resetPasswordRequest.getEmail());
            String message = "Password reset email sent successfully.";
            return new ResponseEntity<>(message, HttpStatus.OK);        
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }       
    }

    // reset password with token
    @PostMapping("/reset-password/confirm")
    public ResponseEntity<String> confirmPasswordReset(@Valid @RequestBody ConfirmPasswordResetRequest confirmPasswordResetRequest) {
        try {
            this.authService.resetPassword(confirmPasswordResetRequest.getToken(), confirmPasswordResetRequest.getNewPassword());
            return new ResponseEntity<>("Password reset successfully.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}