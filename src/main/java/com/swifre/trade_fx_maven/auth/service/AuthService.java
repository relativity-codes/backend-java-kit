package com.swifre.trade_fx_maven.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.swifre.trade_fx_maven.mailing.dto.EmailRequest;
import com.swifre.trade_fx_maven.mailing.service.EmailService;
import com.swifre.trade_fx_maven.user.entity.User;
import com.swifre.trade_fx_maven.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final EmailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Autowired
    public AuthService(
            UserRepository userRepository,
            EmailService mailService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.mailService = mailService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

    /**
     * Verifies the user's email using a token.
     * 
     * @param token The JWT token containing the user's ID.
     * @throws Exception if the user is not found or any other error occurs.
     */
    public void verifyEmail(String token) throws Exception {
        UUID userId = this.jwtService.extractId(token);
        User userDetails = this.userRepository.findById(userId).orElseThrow(() -> new Exception("User not found"));
        userDetails.setEmailVerified(true);
        this.userRepository.save(userDetails);
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

    public Optional<User> updatePassword(UUID userId, String newPassword) {
        return this.userRepository.findById(userId).map(user -> {
            user.setPassword(passwordEncoder.encode(newPassword));
            return this.userRepository.save(user);
        });
    }

    /**
     * Updates an existing user.
     * 
     * @param id          The ID of the user to update.
     * @param userDetails A User object containing the updated details.
     * @return An Optional containing the updated User if found, or empty if not
     *         found.
     */
    public void verifyEmail(UUID userId) {
        this.userRepository.findById(userId).ifPresent(user -> {
            user.setEmailVerified(true); // Set emailVerified to true
            this.userRepository.save(user); // Save the updated user
        });
    }

    public User validateUser(String email, String password) throws Exception {
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new Exception("Invalid password");
        }
        return user;
    }

    public void sendResetPasswordEmail(String email, String resetToken) throws Exception {
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not found"));

        String resetPasswordLink = frontendUrl + "/reset-password?token=" + resetToken;

        this.mailService.sendEmail(new EmailRequest(List.of(user.getEmail()), "Reset Password",
                "Please click the link to reset your password: " + resetPasswordLink));
    }

    public void requestPasswordReset(String email) throws Exception {
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not found"));

        String resetToken = this.jwtService.generateToken(user);
        sendResetPasswordEmail(user.getEmail(), resetToken);
    }

    public void resetPassword(String token, String newPassword) throws Exception {
        try {
            UUID userId = this.jwtService.extractId(token);
            // In a real application, you might want to check for token expiry here,
            // but JWT libraries often handle this automatically during verification.
            this.userRepository.findById(userId).map(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                return this.userRepository.save(user);
            });
        } catch (Exception e) {
            throw new Exception("Invalid or expired reset password token: " + e.getMessage());
        }
    }

    public void changePassword(UUID userId, String oldPassword, String newPassword) throws Exception {
        try {
            User user = this.userRepository.findById(userId)
                    .orElseThrow(() -> new Exception("User not found for password change"));

            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                throw new Exception("Invalid old password");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
            this.userRepository.save(user);
        } catch (NotFoundException e) {
            throw e; // Re-throw NotFoundException
        } catch (Exception e) {
            throw new Exception("Invalid or incorrect old password: " + e.getMessage());
        }
    }
}
