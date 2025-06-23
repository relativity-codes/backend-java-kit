// UserController.java - REST API Controller
package com.swifre.trade_fx_maven.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.swifre.trade_fx_maven.user.entity.User;
import com.swifre.trade_fx_maven.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
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
    @Operation(summary = "Get all users", description = "Retrieves a list of all users in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @ResponseStatus(HttpStatus.OK) // Set response status
    @GetMapping(produces = "application/json") // Specify produces type
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
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
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @ResponseStatus(HttpStatus.OK) // Set response status
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) { // Changed Long to UUID
        return userService.getUserById(id)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
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
            @ApiResponse(responseCode = "201", description = "Successfully created user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user details"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @ResponseStatus(HttpStatus.CREATED) // Set response status
    @PostMapping(produces = "application/json") // Specify produces type
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
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
            @ApiResponse(responseCode = "200", description = "Successfully updated user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid user details"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @ResponseStatus(HttpStatus.OK) // Set response status
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable UUID id, @RequestBody User userDetails) { // Changed Long to
                                                                                                   // UUID
        return userService.updateUser(id, userDetails)
                .map(updatedUser -> new ResponseEntity<>(updatedUser, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
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
            @ApiResponse(responseCode = "204", description = "Successfully deleted user"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT) // Set response status
    @DeleteMapping("/{id}") // Specify the path variable
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) { // Changed Long to UUID
        if (userService.deleteUser(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}