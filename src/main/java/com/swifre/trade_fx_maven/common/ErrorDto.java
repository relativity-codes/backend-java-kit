package com.swifre.trade_fx_maven.common;

public class ErrorDto {
    private String message;
    private String details;
    private String error;

    public ErrorDto(String message, String details, String error) {
        this.message = message;
        this.details = details;
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
