package com.standard.commerce_maven.mailing.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service; // Import Value for 'from' address

import com.standard.commerce_maven.mailing.dto.EmailRequest;

/**
 * Service class responsible for sending emails.
 * Uses Spring's JavaMailSender to interact with an SMTP server.
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}") // Injects the email username from properties as the sender
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a simple text email.
     * 
     * @param to      List of recipient email addresses.
     * @param subject The subject of the email.
     * @param body    The body content of the email.
     */
    public void sendSimpleEmail(List<String> to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail); // Set the 'from' address from application properties
        message.setTo(to.toArray(new String[0])); // Convert list to array
        message.setSubject(subject);
        message.setText(body);

        try {
            mailSender.send(message);
            System.out.println("Email sent successfully to: " + String.join(", ", to));
        } catch (MailException e) {
            System.err.println("Error sending email to " + String.join(", ", to) + ": " + Arrays.toString(e.getStackTrace()));
            // In a real application, you might throw a custom exception, log to a
            // monitoring system, etc.
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Sends a simple text email with optional CC and BCC recipients.
     * 
     * @param emailRequest The EmailRequest DTO containing all email details.
     */
    public void sendEmail(EmailRequest emailRequest) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail); // Set the 'from' address

        if (emailRequest.getTo() != null && !emailRequest.getTo().isEmpty()) {
            message.setTo(emailRequest.getTo().toArray(new String[0]));
        } else {
            throw new IllegalArgumentException("Recipient 'to' list cannot be empty for email.");
        }

        message.setSubject(emailRequest.getSubject());
        message.setText(emailRequest.getBody());

        if (emailRequest.getCc() != null && !emailRequest.getCc().isEmpty()) {
            message.setCc(emailRequest.getCc().toArray(new String[0]));
        }
        if (emailRequest.getBcc() != null && !emailRequest.getBcc().isEmpty()) {
            message.setBcc(emailRequest.getBcc().toArray(new String[0]));
        }

        try {
            mailSender.send(message);
            System.out.println("Email sent successfully. To: " + String.join(", ", emailRequest.getTo()) +
                    (emailRequest.getCc() != null && !emailRequest.getCc().isEmpty()
                            ? ", CC: " + String.join(", ", emailRequest.getCc())
                            : "")
                    +
                    (emailRequest.getBcc() != null && !emailRequest.getBcc().isEmpty()
                            ? ", BCC: " + String.join(", ", emailRequest.getBcc())
                            : ""));
        } catch (MailException e) {
            System.err.println("Error sending email: " + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException("Failed to send email", e);
        }
    }

    // In a real-world scenario, you might want to add methods for:
    // - Sending HTML emails (using MimeMessageHelper)
    // - Sending emails with attachments
    // - Using templating engines (Thymeleaf, FreeMarker) for rich email content
}
