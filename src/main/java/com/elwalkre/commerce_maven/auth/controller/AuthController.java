package com.elwalkre.commerce_maven.auth.controller;
import com.elwalkre.commerce_maven.common.ResponseDto;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.elwalkre.commerce_maven.auth.dto.AuthRequest;
import com.elwalkre.commerce_maven.auth.dto.AuthResponse;
import com.elwalkre.commerce_maven.auth.dto.ConfirmPasswordResetRequest;
import com.elwalkre.commerce_maven.auth.dto.ResetPasswordRequest;
import com.elwalkre.commerce_maven.auth.dto.UpdateEmailRequest;
import com.elwalkre.commerce_maven.auth.dto.UpdatePasswordRequest;
import com.elwalkre.commerce_maven.auth.service.JwtService;
import com.elwalkre.commerce_maven.user.dtos.UserDto;
import com.elwalkre.commerce_maven.user.entity.User;
import com.elwalkre.commerce_maven.user.enums.UserType;
import com.elwalkre.commerce_maven.user.service.UserService;
import com.elwalkre.commerce_maven.auth.service.AuthService;

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
        @ApiResponse(responseCode = "200", description = "User authenticated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error - Authentication failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<ResponseDto<AuthResponse>> authenticate(@Valid @RequestBody AuthRequest authRequest) {
        ResponseDto<AuthResponse> response = new ResponseDto<>();
        try {
            User user = this.authService.validateUser(
                    authRequest.getEmail(),
                    authRequest.getPassword());

            if (user != null) {
                String jwt = this.jwtService.generateToken(user);
                response.setData(new AuthResponse(jwt, user));
                response.setStatus(200);
                response.setMessage("Login successful");
                response.setError(null);
            } else {
                response.setData(null);
                response.setStatus(401);
                response.setMessage("Invalid credentials");
                response.setError("Unauthorized");
            }
        } catch (Exception e) {
            response.setData(null);
            response.setStatus(500);
            response.setMessage("Authentication failed");
            response.setError(Arrays.toString(e.getStackTrace()));
        }
        return ResponseEntity.ok(response);
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
        @ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request - Invalid user data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error - Registration failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<ResponseDto<User>> registerUser(@Valid @RequestBody UserDto user) {
        ResponseDto<User> response = new ResponseDto<>();
        try {
            User userEntity = new User();
            userEntity.setUsername(user.getUsername());
            userEntity.setEmail(user.getEmail());
            userEntity.setPassword(user.getPassword());
            UserType userType = UserType.USER;
            userEntity.setUserType(userType);
            User createdUser = this.userService.createUser(userEntity);
            response.setData(createdUser);
            response.setStatus(201);
            response.setMessage("User registered successfully");
            response.setError(null);
        } catch (Exception e) {
            response.setData(null);
            response.setStatus(500);
            response.setMessage("Registration failed");
            response.setError(Arrays.toString(e.getStackTrace()));
        }
        return ResponseEntity.ok(response);
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
        @ApiResponse(responseCode = "200", description = "Email verified successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request - Invalid token", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Not Found - User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error - Verification failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class)))
    })
    @PostMapping("/verify-email")
    public ResponseEntity<ResponseDto<String>> verifyEmail(@RequestParam("token") String token) {
        ResponseDto<String> response = new ResponseDto<>();
        try {
            this.authService.verifyEmail(token);
            response.setData("Email verified successfully");
            response.setStatus(200);
            response.setMessage("Email verified successfully");
            response.setError(null);
        } catch (Exception e) {
            response.setData(null);
            response.setStatus(500);
            response.setMessage("Verification failed");
            response.setError(Arrays.toString(e.getStackTrace()));
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Example: Endpoint to get the current authenticated user's details.
     * This would typically be used to retrieve user information after login.
     * 
     * @return ResponseEntity with the current user's details.
     */
    @GetMapping("/me")
    public ResponseEntity<ResponseDto<User>> getCurrentUser() {
        ResponseDto<User> response = new ResponseDto<>();
        User user = this.authService.getCurrentUser();
        if (user != null) {
            response.setData(user);
            response.setStatus(200);
            response.setMessage("User fetched successfully");
            response.setError(null);
        } else {
            response.setData(null);
            response.setStatus(401);
            response.setMessage("Unauthorized");
            response.setError("Unauthorized");
        }
        return ResponseEntity.ok(response);
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
    public ResponseEntity<ResponseDto<String>> updatePassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest) {
        ResponseDto<String> response = new ResponseDto<>();
        try {
            User user = this.authService.getCurrentUser();
            if (user == null) {
                response.setData(null);
                response.setStatus(401);
                response.setMessage("Unauthorized");
                response.setError("Unauthorized");
                return ResponseEntity.ok(response);
            }
            this.authService.changePassword(user.getId(), updatePasswordRequest.getOldPassword(),
                    updatePasswordRequest.getNewPassword());
            response.setData("Password updated successfully");
            response.setStatus(200);
            response.setMessage("Password updated successfully");
            response.setError(null);
        } catch (Exception e) {
            response.setData(null);
            response.setStatus(500);
            response.setMessage("Password update failed");
            response.setError(Arrays.toString(e.getStackTrace()));
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/me/email")
    public ResponseEntity<ResponseDto<String>> updateEmail(@Valid @RequestBody UpdateEmailRequest updateEmailRequest) {
        ResponseDto<String> response = new ResponseDto<>();
        try {
            User user = this.authService.getCurrentUser();
            if (user == null) {
                response.setData(null);
                response.setStatus(401);
                response.setMessage("Unauthorized");
                response.setError("Unauthorized");
                return ResponseEntity.ok(response);
            }
            this.authService.updateEmail(user.getId(), updateEmailRequest.getEmail());
            response.setData("Email updated successfully");
            response.setStatus(200);
            response.setMessage("Email updated successfully");
            response.setError(null);
        } catch (Exception e) {
            response.setData(null);
            response.setStatus(500);
            response.setMessage("Email update failed");
            response.setError(Arrays.toString(e.getStackTrace()));
        }
        return ResponseEntity.ok(response);
    }

    // reset password request email
    @PostMapping("/reset-password")
    public ResponseEntity<ResponseDto<String>> requestPasswordReset(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        ResponseDto<String> response = new ResponseDto<>();
        try {
            this.authService.requestPasswordReset(resetPasswordRequest.getEmail());
            response.setData("Password reset email sent successfully.");
            response.setStatus(200);
            response.setMessage("Password reset email sent successfully.");
            response.setError(null);
        } catch (Exception e) {
            response.setData(null);
            response.setStatus(500);
            response.setMessage("Password reset request failed");
            response.setError(Arrays.toString(e.getStackTrace()));
        }
        return ResponseEntity.ok(response);
    }

    // reset password with token
    @PostMapping("/reset-password/confirm")
    public ResponseEntity<ResponseDto<String>> confirmPasswordReset(@Valid @RequestBody ConfirmPasswordResetRequest confirmPasswordResetRequest) {
        ResponseDto<String> response = new ResponseDto<>();
        try {
            this.authService.resetPassword(confirmPasswordResetRequest.getToken(), confirmPasswordResetRequest.getNewPassword());
            response.setData("Password reset successfully.");
            response.setStatus(200);
            response.setMessage("Password reset successfully.");
            response.setError(null);
        } catch (Exception e) {
            response.setData(null);
            response.setStatus(500);
            response.setMessage("Password reset failed");
            response.setError(Arrays.toString(e.getStackTrace()));
        }
        return ResponseEntity.ok(response);
    }
}