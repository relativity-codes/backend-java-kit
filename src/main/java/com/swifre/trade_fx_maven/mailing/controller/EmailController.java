package com.swifre.trade_fx_maven.mailing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swifre.trade_fx_maven.mailing.dto.EmailRequest;
import com.swifre.trade_fx_maven.mailing.service.EmailService;

/**
 * REST Controller for triggering email sending.
 */
@RestController
@RequestMapping("/api/mailing") // Base path for email-related endpoints
public class EmailController {

    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * POST /api/email/send
     * Endpoint to send an email based on the provided EmailRequest.
     * Requires authentication (handled by Spring Security configured previously).
     *
     * @param emailRequest The DTO containing recipient, subject, and body.
     * @return ResponseEntity indicating success or failure.
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest emailRequest) {
        try {
            emailService.sendEmail(emailRequest);
            return new ResponseEntity<>("Email sent successfully!", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            // Catching the custom RuntimeException thrown by EmailService
            return new ResponseEntity<>("Failed to send email: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
