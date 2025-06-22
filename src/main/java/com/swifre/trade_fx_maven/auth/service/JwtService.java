// JwtService.java - Service for JWT operations
package com.swifre.trade_fx_maven.auth.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.swifre.trade_fx_maven.user.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Service for handling JWT (JSON Web Token) related operations:
 * - Generating JWTs
 * - Extracting claims from JWTs
 * - Validating JWTs
 */
@Service
public class JwtService {

    // Retrieve JWT secret key from application.properties
    @Value("${application.security.jwt.secret_key}")
    private String secretKey;

    // Retrieve JWT expiration time from application.properties
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration; // in milliseconds

    /**
     * Extracts the id from a JWT.
     * 
     * @param token The JWT.
     * @return The id (subject) from the token.
     */
    public UUID extractId(String token) {
        return UUID.fromString(extractClaim(token, Claims::getSubject));
    }

    /**
     * Extracts a specific claim from the JWT.
     * 
     * @param token          The JWT.
     * @param claimsResolver Function to resolve the desired claim.
     * @param <T>            Type of the claim.
     * @return The extracted claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates a JWT for a given User.
     * 
     * @param user The User object for which to generate the token.
     * @return The generated JWT string.
     */
    public String generateToken(User user) {
        return generateToken(new HashMap<>(), user);
    }

    /**
     * Generates a JWT with extra claims and a User object.
     * 
     * @param extraClaims Additional claims to include in the token.
     * @param user        The User object.
     * @return The generated JWT string.
     */
    public String generateToken(Map<String, Object> extraClaims, User user) {
        return Jwts
                .builder()
                .claims(extraClaims) // Add any extra claims
                .subject(user.getId().toString()) // Set subject (user ID)
                .issuedAt(new Date(System.currentTimeMillis())) // Set issuance time
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Set expiration time
                .signWith(getSignInKey()) // Sign the token with the secret key
                .compact();
    }

    /**
     * Validates a JWT against a User object.
     * 
     * @param token The JWT to validate.
     * @param user  The User object to compare against.
     * @return True if the token is valid, false otherwise.
     */
    public boolean isTokenValid(String token, User user) {
        final UUID id = extractId(token);
        return (id.equals(user.getId()) && !isTokenExpired(token));
    }

    /**
     * Checks if a JWT is expired.
     * 
     * @param token The JWT to check.
     * @return True if the token is expired, false otherwise.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from a JWT.
     * 
     * @param token The JWT.
     * @return The expiration Date.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts all claims from a JWT.
     * 
     * @param token The JWT.
     * @return All claims as a Claims object.
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey()) // Verify token with the secret key
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Decodes the secret key from Base64 and returns it as a SecretKey.
     * 
     * @return The SecretKey used for signing.
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}