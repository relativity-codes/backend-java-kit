// UserController.java - REST API Controller
package com.swifre.trade_fx_maven.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.swifre.trade_fx_maven.user.entity.User;
import com.swifre.trade_fx_maven.user.service.UserService;

import java.util.List;
import java.util.UUID; // Import UUID

/**
 * REST Controller for User management.
 * Exposes endpoints for CRUD operations on User entities.
 */
@RestController
@RequestMapping("/api/users") // Base path for all user-related endpoints
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
    @GetMapping
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
    @PostMapping
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) { // Changed Long to UUID
        if (userService.deleteUser(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}