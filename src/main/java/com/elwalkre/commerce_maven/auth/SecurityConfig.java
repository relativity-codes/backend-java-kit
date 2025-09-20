package com.elwalkre.commerce_maven.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.elwalkre.commerce_maven.auth.service.AuthService;
import com.elwalkre.commerce_maven.common.SecurityRules;

import java.util.List;

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
    private final List<SecurityRules> featureSecurityRules;

    /**
     * Constructor for SecurityConfig.
     * 
     * @param jwtAuthFilter The custom JWT authentication filter.
     * @param authService   The service to load user details.
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, AuthService authService,
            List<SecurityRules> featureSecurityRules) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authService = authService;
        this.featureSecurityRules = featureSecurityRules;
    }

    /**
     * Configures the security filter chain.
     * 
     * @param http HttpSecurity to configure.
     * @return The built SecurityFilterChain.
     * @throws Exception if an error occurs during configuration.
     */
    @SuppressWarnings("unused")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Use stateless sessions
                )
                .authorizeHttpRequests(c -> {
                    c.requestMatchers("/**").permitAll()
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**","/public/**", "/login", "/register").permitAll()
                    .requestMatchers("/h2-console/**").permitAll();
                    this.featureSecurityRules.forEach(r -> r.configure(c));
                })
                .addFilterBefore(this.jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(c -> {
                    c.authenticationEntryPoint(
                            new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
                    c.accessDeniedHandler(((request, response, accessDeniedException) -> response
                            .setStatus(HttpStatus.FORBIDDEN.value())));
                });

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