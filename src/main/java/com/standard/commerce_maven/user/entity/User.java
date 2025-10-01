package com.standard.commerce_maven.user.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections; // Import EnumType
import java.util.Objects; // Import Enumerated
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.standard.commerce_maven.user.enums.UserType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType; // Import UserType enum
import jakarta.persistence.Enumerated; // Import Collection
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id; // Import Collections
import jakarta.persistence.PrePersist; // Import GrantedAuthority
import jakarta.persistence.PreUpdate; // Import UserDetails
import jakarta.persistence.Table;

/**
 * Represents a User entity in the application.
 * This class maps to a table in the database and implements UserDetails for
 * Spring Security.
 */
@Entity
@Table(name = "users")
public class User implements UserDetails { // Implemented UserDetails

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType = UserType.USER; // Default to USER

    @Column(name = "email_verified_at", nullable = true)
    private LocalDateTime emailVerifiedAt;

    @Column(name = "two_factor_secret", nullable = true)
    private String twoFactorSecret;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public User() {
        // this.id = UUID.randomUUID(); // Generate UUID on object creation
    }

    public User(String username, String email, String password, UserType userType) {
        this(); // Call default constructor to initialize id
        this.username = username;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    /**
     * Sets default values before persisting.
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) this.createdAt = now;
        if (this.updatedAt == null) this.updatedAt = now;
        if (this.userType == null) this.userType = UserType.USER;
    }

    /**
     * Updates the updatedAt timestamp before updating.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.userType == null) this.userType = UserType.USER;
    }

    // Getters
    public UUID getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    public UserType getUserType() {
        return this.userType;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    // Setters
    public void setId(UUID id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    // UserDetails interface implementations
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(this.userType);
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // For simplicity, accounts never expire
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // For simplicity, accounts are never locked
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // For simplicity, credentials never expire
    }

    @Override
    public boolean isEnabled() {
        return true; // For simplicity, accounts are always enabled
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(this.id, user.id) &&
                Objects.equals(this.username, user.username) &&
                Objects.equals(this.email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.username, this.email, this.password, this.userType, this.emailVerifiedAt, this.createdAt, this.updatedAt);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + this.id +
                ", username='" + this.username + '\'' +
                ", email='" + this.email + '\'' +
                ", password='[PROTECTED]'" +
                ", userType='" + this.userType + '\'' +
                ", emailVerifiedAt='" + this.emailVerifiedAt + '\'' +
                ", createdAt=" + this.createdAt +
                ", updatedAt=" + this.updatedAt +
                '}';
    }

    public boolean isEmailVerified() {
        return this.emailVerifiedAt != null;
    }

    /**
     * Sets the email verification status.
     *
     * @param emailVerified true if the email is verified, false otherwise.
     */
    public void setEmailVerifiedAt(LocalDateTime emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public String getTwoFactorSecret() {
        return this.twoFactorSecret;
    }

    public void setTwoFactorSecret(String twoFactorSecret) {
        this.twoFactorSecret = twoFactorSecret;
    }
}