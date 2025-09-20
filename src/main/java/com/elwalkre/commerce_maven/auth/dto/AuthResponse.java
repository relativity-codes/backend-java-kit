package com.elwalkre.commerce_maven.auth.dto;

import com.elwalkre.commerce_maven.user.entity.User;

import java.util.Objects;

/**
 * Data Transfer Object for user login responses, containing the JWT.
 */
public class AuthResponse {
    private String jwtToken;
    private User user;

    public AuthResponse() {
    }

    public AuthResponse(String jwtToken, User user) {
        this.jwtToken = jwtToken;
        this.user = user;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AuthResponse that = (AuthResponse) o;
        return Objects.equals(jwtToken, that.jwtToken) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jwtToken, user);
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "jwtToken='" + jwtToken + '\'' +
                ", user=" + user +
                '}';
    }
}
