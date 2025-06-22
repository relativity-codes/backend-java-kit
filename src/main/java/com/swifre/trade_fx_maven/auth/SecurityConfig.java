package com.swifre.trade_fx_maven.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // For @PreAuthorize
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.swifre.trade_fx_maven.auth.service.AuthService;

/**
 * Spring Security configuration class.
 * - Disables CSRF
 * - Configures stateless session management
 * - Defines authentication provider (DaoAuthenticationProvider)
 * - Adds custom JWT authentication filter
 * - Sets up authorization rules for endpoints
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enables method-level security (e.g., @PreAuthorize)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthService authService;

    /**
     * Constructor for SecurityConfig.
     * 
     * @param jwtAuthFilter The custom JWT authentication filter.
     * @param authService   The service to load user details.
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, AuthService authService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authService = authService;
    }

    /**
     * Configures the security filter chain.
     * 
     * @param http HttpSecurity to configure.
     * @return The built SecurityFilterChain.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for stateless API
                .authorizeHttpRequests(auth -> auth
                        // Permit access to authentication endpoint and H2 console
                        .requestMatchers("/api/auth/**", "/h2-console/**").permitAll()
                        // Require authentication for all other API endpoints
                        .requestMatchers("/api/**").authenticated()
                        // Deny all other requests by default (if not matched above)
                        .anyRequest().denyAll())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Use stateless sessions
                )
                .authenticationProvider(authenticationProvider()) // Set custom authentication provider
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter before
                                                                                             // UsernamePasswordAuthenticationFilter

        // For H2 console to work with Spring Security (since it uses iframes)
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }

    /**
     * Provides a BCryptPasswordEncoder bean for password hashing.
     * 
     * @return A BCryptPasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the AuthenticationProvider to use UserDetailsService and
     * PasswordEncoder.
     * 
     * @return A DaoAuthenticationProvider instance.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(authService); // Set our custom UserDetailsService
        authProvider.setPasswordEncoder(passwordEncoder()); // Set our password encoder
        return authProvider;
    }

    /**
     * Provides the AuthenticationManager bean, which handles authentication
     * requests.
     * 
     * @param config AuthenticationConfiguration from Spring.
     * @return The AuthenticationManager instance.
     * @throws Exception if an error occurs.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}