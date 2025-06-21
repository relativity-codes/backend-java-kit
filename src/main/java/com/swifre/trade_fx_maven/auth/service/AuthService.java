package com.swifre.trade_fx_maven.auth.service;

import org.springframework.stereotype.Service;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.swifre.trade_fx_maven.mailing.dto.EmailRequest;
import com.swifre.trade_fx_maven.mailing.service.EmailService;
import com.swifre.trade_fx_maven.user.entity.User;
import com.swifre.trade_fx_maven.user.service.UserService;
import java.util.List;

@Service
public class AuthService {

    private final UserService userService;
    private final EmailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Autowired
    public AuthService(EmailService mailService, UserService userService, PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.mailService = mailService;
        this.jwtService = jwtService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public void verifyEmail(String token) throws Exception {
        UUID userId = this.jwtService.extractId(token);
        User userDetails = this.userService.getUserById(userId).orElseThrow(() -> new Exception("User not found"));
        userDetails.setEmailVerified(true);
        this.userService.updateUser(userId, userDetails);
    }

    public User validateUser(String email, String password) throws Exception {
        User user = this.userService.findOneByEmail(email)
                .orElseThrow(() -> new Exception("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new Exception("Invalid password");
        }
        return user;
    }

    public void sendResetPasswordEmail(String email, String resetToken) throws Exception {
        User user = this.userService.findOneByEmail(email)
                .orElseThrow(() -> new Exception("User not found"));

        String resetPasswordLink = frontendUrl + "/reset-password?token=" + resetToken;

        this.mailService.sendEmail(new EmailRequest(List.of(user.getEmail()), "Reset Password",
                "Please click the link to reset your password: " + resetPasswordLink));
    }

    public void requestPasswordReset(String email) throws Exception {
        User user = this.userService.findOneByEmail(email)
                .orElseThrow(() -> new Exception("User not found"));

        String resetToken = this.jwtService.generateToken(user);
        sendResetPasswordEmail(user.getEmail(), resetToken);
    }

    public void resetPassword(String token, String newPassword) throws Exception {
        try {
            UUID userId = this.jwtService.extractId(token);
            // In a real application, you might want to check for token expiry here,
            // but JWT libraries often handle this automatically during verification.
            this.userService.updatePassword(userId, newPassword);
        } catch (Exception e) {
            throw new Exception("Invalid or expired reset password token: " + e.getMessage());
        }
    }

    public void changePassword(UUID userId, String oldPassword, String newPassword) throws Exception {
        try {
            User user = this.userService.getUserById(userId)
                    .orElseThrow(() -> new Exception("User not found for password change"));

            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                throw new Exception("Invalid old password");
            }
            this.userService.updatePassword(userId, newPassword);
        } catch (NotFoundException e) {
            throw e; // Re-throw NotFoundException
        } catch (Exception e) {
            throw new Exception("Invalid or incorrect old password: " + e.getMessage());
        }
    }
}
