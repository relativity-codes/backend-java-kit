package com.standard.commerce_maven.mailing.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.standard.commerce_maven.common.ResponseDto;
import com.standard.commerce_maven.mailing.dto.EmailRequest;
import com.standard.commerce_maven.mailing.service.EmailService;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST Controller for triggering email sending.
 */
@RestController
@Tag(name = "Emailing Management", description = "Operations related to email management")
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
    public ResponseEntity<ResponseDto<String>> sendEmail(@RequestBody EmailRequest emailRequest) {
        ResponseDto<String> response = new ResponseDto<>();
        try {
            emailService.sendEmail(emailRequest);
            response.setData("Email sent successfully!");
            response.setStatus(200);
            response.setMessage("Email sent successfully");
            response.setError(null);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.setData(null);
            response.setStatus(400);
            response.setMessage("Invalid email request");
            response.setError(Arrays.toString(e.getStackTrace()));
            return ResponseEntity.badRequest().body(response);
        } catch (RuntimeException e) {
            response.setData(null);
            response.setStatus(500);
            response.setMessage("Failed to send email");
            response.setError(Arrays.toString(e.getStackTrace()));
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
