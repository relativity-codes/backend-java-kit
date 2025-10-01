// UserRepository.java - Data Access Layer
package com.standard.commerce_maven.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.standard.commerce_maven.user.entity.User;

import java.util.Optional;
import java.util.UUID; // Import UUID

/**
 * Repository interface for User entities.
 * Extends JpaRepository to provide standard CRUD operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> { // Changed Long to UUID
    // Custom query methods can be added here if needed, e.g., findByUsername,
    // findByEmail
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}