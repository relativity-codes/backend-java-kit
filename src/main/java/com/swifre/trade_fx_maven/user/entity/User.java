package com.swifre.trade_fx_maven.user.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections; // Import EnumType
import java.util.Objects; // Import Enumerated
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.swifre.trade_fx_maven.user.enums.UserType;

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
    private UserType userType;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "two_factor_secret", nullable = true)
    private String twoFactorSecret;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public User() {
        this.id = UUID.randomUUID(); // Generate UUID on object creation
    }

    public User(String username, String email, String password, UserType userType) {
        this(); // Call default constructor to initialize id
        this.username = username;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    /**
     * Sets createdAt and updatedAt timestamps before persisting.
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * Updates the updatedAt timestamp before updating.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public UserType getUserType() {
        return userType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
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
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
        return Objects.equals(id, user.id) &&
                Objects.equals(username, user.username) &&
                Objects.equals(email, user.email) &&
                Objects.equals(password, user.password) &&
                Objects.equals(userType, user.userType) &&
                Objects.equals(emailVerified, user.emailVerified) &&
                Objects.equals(createdAt, user.createdAt) &&
                Objects.equals(updatedAt, user.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, password, userType, emailVerified, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                ", userType='" + userType + '\'' +
                ", emailVerified='" + emailVerified + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    /**
     * Sets the email verification status.
     *
     * @param emailVerified true if the email is verified, false otherwise.
     */
    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getTwoFactorSecret() {
        return twoFactorSecret;
    }

    public void setTwoFactorSecret(String twoFactorSecret) {
        this.twoFactorSecret = twoFactorSecret;
    }
}