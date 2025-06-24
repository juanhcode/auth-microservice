package com.develop.auth_microservice.presentation.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
    
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, String>> handleNullPointerException(NullPointerException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "A required value is missing: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
    }
    @ExceptionHandler(feign.FeignException.class)
    public ResponseEntity<Map<String, String>> handleFeignException(feign.FeignException ex) {
        Map<String, String> errors = new HashMap<>();
        String userMessage = "Servicio de usuarios no disponible";
        int status = HttpStatus.SERVICE_UNAVAILABLE.value();

        try {
            String responseBody = ex.contentUTF8();
            if (responseBody != null && responseBody.contains("\"message\"")) {
                int start = responseBody.indexOf("\"message\":\"") + 11;
                int end = responseBody.indexOf("\"", start);
                userMessage = responseBody.substring(start, end);
            }
            if (responseBody != null && responseBody.contains("\"status\":")) {
                int statusStart = responseBody.indexOf("\"status\":") + 9;
                int statusEnd = responseBody.indexOf(",", statusStart);
                if (statusEnd == -1) statusEnd = responseBody.length() - 1;
                String statusStr = responseBody.substring(statusStart, statusEnd).replaceAll("[^0-9]", "");
                status = Integer.parseInt(statusStr);
            }
        } catch (Exception ignored) {}

        errors.put("error", userMessage);
        return ResponseEntity.status(status).body(errors);
    }

}
