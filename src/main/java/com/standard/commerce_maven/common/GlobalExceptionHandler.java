package com.standard.commerce_maven.common;

import java.util.Arrays;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseDto<String>> handleUnreadableMessage(Exception exception) {
        ResponseDto<String> response = new ResponseDto<>(
                null,
                400,
                exception.getMessage(),
                "Invalid request body"
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException exception) {
        var errors = new HashMap<String, String>();

        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<String>> handleGenericException(Exception exception) {
    ResponseDto<String> response = new ResponseDto<>(
        null,
        500,
        Arrays.toString(exception.getStackTrace()),
        "An unexpected error occurred"
    );
    return ResponseEntity.status(500).body(response);
    }
}