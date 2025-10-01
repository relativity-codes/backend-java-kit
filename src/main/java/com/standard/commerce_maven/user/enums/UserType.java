// UserType.java - Enum for User Roles
package com.standard.commerce_maven.user.enums;

import org.springframework.security.core.GrantedAuthority; // Import GrantedAuthority

/**
 * Enum to define different types/roles of users in the system.
 * Implements GrantedAuthority for Spring Security integration.
 */
public enum UserType implements GrantedAuthority {
    ADMIN,
    SUPER_ADMIN,
    USER;

    @Override
    public String getAuthority() {
        // Spring Security expects roles to start with "ROLE_" prefix by convention
        return "ROLE_" + this.name();
    }
}