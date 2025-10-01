// UserController.java - REST API Controller
package com.standard.commerce_maven.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.standard.commerce_maven.common.ResponseDto;
import com.standard.commerce_maven.user.dtos.UserDto;
import com.standard.commerce_maven.user.entity.User;
import com.standard.commerce_maven.user.enums.UserType;
import com.standard.commerce_maven.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.Arrays;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.UUID; // Import UUID

/**
 * REST Controller for User management.
 * Exposes endpoints for CRUD operations on User entities.
 */
@RestController
@RequestMapping("/api/users") // Base path for all user-related endpoints
@Tag(name = "User Management", description = "Operations related to user management")
public class UserController {

    private final UserService userService;

    /**
     * Constructs a UserController with a UserService.
     * Spring will automatically inject the UserService.
     * 
     * @param userService The service for user business logic.
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET /api/users
     * Retrieves all users.
     * 
     * @return A ResponseEntity containing a list of users and HTTP status OK.
     */
    @Operation(summary = "Get all users (paginated)", description = "Retrieves a paginated list of users in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated list of users", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class)))
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = "application/json")
    public ResponseEntity<ResponseDto<Page<User>>> getAllUsers(
        @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(value = "page", required = false, defaultValue = "0") int page,
        @Parameter(description = "Page size", example = "10") @RequestParam(value = "size", required = false, defaultValue = "10") int size,
        @Parameter(description = "Sort criteria (e.g. username,asc)", example = "username,asc") @RequestParam(value = "sort", required = false) String[] sort
    ) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(sort != null && sort.length > 0 ? sort[0] : "id"));
        ResponseDto<Page<User>> response = new ResponseDto<>();
        try {
            Page<User> users = this.userService.getAllUsers(pageable);
            response.setData(users);
            response.setStatus(200);
            response.setMessage("Users retrieved successfully");
            response.setError(null);
        } catch (Exception e) {
            response.setData(null);
            response.setStatus(500);
            response.setMessage("Failed to retrieve users");
            response.setError(Arrays.toString(e.getStackTrace()));
        }
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/users/{id}
     * Retrieves a user by their ID.
     * 
     * @param id The ID of the user to retrieve.
     * @return A ResponseEntity containing the user and HTTP status OK if found,
     *         or HTTP status NOT_FOUND if not found.
     */
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their unique ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class)))
    })
    @ResponseStatus(HttpStatus.OK) // Set response status
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<User>> getUserById(@PathVariable UUID id) { // Changed Long to UUID
        ResponseDto<User> response = new ResponseDto<>();
        try {
            return this.userService.getUserById(id)
                .map(user -> {
                    response.setData(user);
                    response.setStatus(200);
                    response.setMessage("User retrieved successfully");
                    response.setError(null);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    response.setData(null);
                    response.setStatus(404);
                    response.setMessage("User not found");
                    response.setError(null);
                    return ResponseEntity.ok(response);
                });
        } catch (Exception e) {
            response.setData(null);
            response.setStatus(500);
            response.setMessage("Failed to retrieve user");
            response.setError(Arrays.toString(e.getStackTrace()));
            return ResponseEntity.ok(response);
        }
    }

/**
     * POST /api/users
     * Creates a new user.
     * 
     * @param user The User object to create (from request body).
     * @return A ResponseEntity containing the created user and HTTP status CREATED.
     */
    @Operation(summary = "Create a new user", description = "Creates a new user in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user details", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class)))
    })
    @ResponseStatus(HttpStatus.CREATED) // Set response status
    @PostMapping(produces = "application/json") // Specify produces type
    public ResponseEntity<ResponseDto<User>> createUser(@Valid @RequestBody UserDto user) {
                ResponseDto<User> response = new ResponseDto<>();
                try {
                        User userEntity = new User();
                        userEntity.setUsername(user.getUsername());
                        userEntity.setEmail(user.getEmail());
                        userEntity.setPassword(user.getPassword());
                        UserType userType = user.getUserType() != null ? UserType.valueOf(user.getUserType().toUpperCase())
                                        : UserType.USER;
                        userEntity.setUserType(userType);
                        User createdUser = this.userService.createUser(userEntity);
                        response.setData(createdUser);
                        response.setStatus(201);
                        response.setMessage("User created successfully");
                        response.setError(null);
                } catch (Exception e) {
                        response.setData(null);
                        response.setStatus(500);
                        response.setMessage("Failed to create user");
                        response.setError(Arrays.toString(e.getStackTrace()));
                }
                return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/users/{id}
     * Updates an existing user.
     * 
     * @param id          The ID of the user to update.
     * @param userDetails The User object with updated details (from request body).
     * @return A ResponseEntity containing the updated user and HTTP status OK if
     *         found,
     *         or HTTP status NOT_FOUND if not found.
     */

    @Operation(summary = "Update user by ID", description = "Updates an existing user by their unique ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user details", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class)))
    })
    @ResponseStatus(HttpStatus.OK) // Set response status
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<User>> updateUser(@PathVariable UUID id, @Valid @RequestBody UserDto userDetails) { // Changed
                                                                                                             // Long to
        // UUID
        ResponseDto<User> response = new ResponseDto<>();
        try {
            User userEntity = new User();
            userEntity.setUsername(userDetails.getUsername());
            userEntity.setEmail(userDetails.getEmail());
            userEntity.setPassword(userDetails.getPassword());
            UserType userType = userDetails.getUserType() != null
                    ? UserType.valueOf(userDetails.getUserType().toUpperCase())
                    : UserType.USER;
            userEntity.setUserType(userType);
            return userService.updateUser(id, userEntity)
                .map(updatedUser -> {
                    response.setData(updatedUser);
                    response.setStatus(200);
                    response.setMessage("User updated successfully");
                    response.setError(null);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    response.setData(null);
                    response.setStatus(404);
                    response.setMessage("User not found");
                    response.setError(null);
                    return ResponseEntity.ok(response);
                });
        } catch (Exception e) {
            response.setData(null);
            response.setStatus(500);
            response.setMessage("Failed to update user");
            response.setError(Arrays.toString(e.getStackTrace()));
            return ResponseEntity.ok(response);
        }
    }

    /**
     * DELETE /api/users/{id}
     * Deletes a user by their ID.
     * 
     * @param id The ID of the user to delete.
     * @return A ResponseEntity with HTTP status NO_CONTENT if deleted,
     *         or HTTP status NOT_FOUND if not found.
     */
    @Operation(summary = "Delete user by ID", description = "Deletes a user by their unique ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class)))
    })
    @ResponseStatus(HttpStatus.NO_CONTENT) // Set response status
    @DeleteMapping("/{id}") // Specify the path variable
    public ResponseEntity<ResponseDto<Void>> deleteUser(@PathVariable UUID id) { // Changed Long to UUID
        ResponseDto<Void> response = new ResponseDto<>();
        try {
            if (userService.deleteUser(id)) {
                response.setData(null);
                response.setStatus(204);
                response.setMessage("User deleted successfully");
                response.setError(null);
            } else {
                response.setData(null);
                response.setStatus(404);
                response.setMessage("User not found");
                response.setError(null);
            }
        } catch (Exception e) {
            response.setData(null);
            response.setStatus(500);
            response.setMessage("Failed to delete user");
            response.setError(Arrays.toString(e.getStackTrace()));
        }
        return ResponseEntity.ok(response);
    }
}