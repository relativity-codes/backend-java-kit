// UserService.java - Business Logic Layer
package com.swifre.trade_fx_maven.user.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder; // Import UUID
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swifre.trade_fx_maven.user.entity.User;
import com.swifre.trade_fx_maven.user.repository.UserRepository;

/**
 * Service layer for User-related business operations.
 * Handles interactions between the controller and the repository.
 * Implements UserDetailsService for Spring Security to load user-specific data.
 */
@Service
public class UserService implements UserDetailsService { // Implemented UserDetailsService

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Injected PasswordEncoder

    /**
     * Constructs a UserService with a UserRepository and PasswordEncoder.
     * Spring will automatically inject these.
     * 
     * @param userRepository  The repository for user data.
     * @param passwordEncoder The encoder for hashing passwords.
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieves all users from the database.
     * 
     * @return A list of all User objects.
     */
    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    /**
     * Retrieves a user by their ID.
     * 
     * @param id The ID of the user to retrieve.
     * @return An Optional containing the User if found, or empty if not found.
     */
    public Optional<User> getUserById(UUID id) throws RuntimeException {
        try {
            return this.userRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving user by ID: " + id, e);
        }
    }

    /**
     * Creates a new user in the database.
     * The password will be encoded before saving.
     * 
     * @param user The User object to be created.
     * @return The saved User object with its generated ID.
     */
    @Transactional
    public User createUser(User user) {
        // Encode the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // emailVerified defaults to false in User entity constructor
        return this.userRepository.save(user);
    }

    /**
     * Updates an existing user.
     * 
     * @param id          The ID of the user to update.
     * @param userDetails A User object containing the updated details.
     * @return An Optional containing the updated User if found, or empty if not
     *         found.
     */
    @Transactional
    public Optional<User> updateUser(UUID id, User userDetails) {
        return this.userRepository.findById(id).map(user -> {
            user.setUsername(userDetails.getUsername());
            user.setEmail(userDetails.getEmail());
            // Only update password if a new one is provided and not empty
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(this.passwordEncoder.encode(userDetails.getPassword()));
            }
            user.setUserType(userDetails.getUserType());
            user.setEmailVerified(userDetails.isEmailVerified()); // Allow updating verification status
            return this.userRepository.save(user);
        });
    }

    /**
     * Deletes a user by their ID.
     * 
     * @param id The ID of the user to delete.
     * @return True if the user was found and deleted, false otherwise.
     */
    @Transactional
    public boolean deleteUser(UUID id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Finds a user by their username.
     * 
     * @param username The username to search for.
     * @return The User object if found, null otherwise.
     */
    public User findByUsername(String username) {
        return this.userRepository.findByUsername(username).orElse(null);
    }

    /**
     * Implements Spring Security's UserDetailsService to load user details by
     * username.
     * 
     * @param username The username to load.
     * @return UserDetails object representing the loaded user.
     * @throws UsernameNotFoundException if the user is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return user; // Our User entity already implements UserDetails
    }

    public Optional<User> findOneByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public Optional<User> updatePassword(UUID userId, String newPassword) {
        return this.userRepository.findById(userId).map(user -> {
            user.setPassword(this.passwordEncoder.encode(newPassword));
            return this.userRepository.save(user);
        });
    }

    public void verifyEmail(UUID userId) {
        this.userRepository.findById(userId).ifPresent(user -> {
            user.setEmailVerified(true); // Set emailVerified to true
            this.userRepository.save(user); // Save the updated user
        });
    }
}