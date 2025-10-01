package com.standard.commerce_maven.mailing.dto;

import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object for requesting email sending.
 */
public class EmailRequest {
    private List<String> to;
    private String subject;
    private String body;
    private List<String> cc; // Optional CC recipients
    private List<String> bcc; // Optional BCC recipients

    public EmailRequest() {
    }

    public EmailRequest(List<String> to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    public EmailRequest(List<String> to, String subject, String body, List<String> cc, List<String> bcc) {
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.cc = cc;
        this.bcc = bcc;
    }

    // Getters
    public List<String> getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public List<String> getCc() {
        return cc;
    }

    public List<String> getBcc() {
        return bcc;
    }

    // Setters
    public void setTo(List<String> to) {
        this.to = to;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setCc(List<String> cc) {
        this.cc = cc;
    }

    public void setBcc(List<String> bcc) {
        this.bcc = bcc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EmailRequest that = (EmailRequest) o;
        return Objects.equals(to, that.to) &&
                Objects.equals(subject, that.subject) &&
                Objects.equals(body, that.body) &&
                Objects.equals(cc, that.cc) &&
                Objects.equals(bcc, that.bcc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(to, subject, body, cc, bcc);
    }

    @Override
    public String toString() {
        return "EmailRequest{" +
                "to=" + to +
                ", subject='" + subject + '\'' +
                ", body.length=" + (body != null ? body.length() : "null") + // Avoid logging full body
                ", cc=" + cc +
                ", bcc=" + bcc +
                '}';
    }
}