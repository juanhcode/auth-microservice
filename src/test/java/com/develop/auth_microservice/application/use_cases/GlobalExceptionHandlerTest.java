package com.develop.auth_microservice.application.use_cases;

import com.develop.auth_microservice.presentation.exceptions.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleValidationExceptions_returnsFieldErrors() {
        FieldError fieldError = new FieldError("user", "email", "El correo es inválido");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("email"));
        assertEquals("El correo es inválido", response.getBody().get("email"));
    }

    @Test
    void handleIllegalArgumentException_returnsBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("ID inválido");

        ResponseEntity<Map<String, String>> response = handler.handleIllegalArgumentException(ex);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("ID inválido", response.getBody().get("error"));
    }

    @Test
    void handleNullPointerException_returnsInternalServerError() {
        NullPointerException ex = new NullPointerException("token");

        ResponseEntity<Map<String, String>> response = handler.handleNullPointerException(ex);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("A required value is missing: token", response.getBody().get("error"));
    }

    @ExceptionHandler(feign.FeignException.class)
    public ResponseEntity<Map<String, String>> handleFeignException(feign.FeignException ex) {
        Map<String, String> errors = new HashMap<>();
        String userMessage = "Servicio de usuarios no disponible";
        int status = HttpStatus.SERVICE_UNAVAILABLE.value();

        try {
            String responseBody = ex.contentUTF8();
            if (responseBody != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> map = objectMapper.readValue(responseBody, Map.class);
                if (map.containsKey("message")) {
                    userMessage = map.get("message").toString();
                }
                if (map.containsKey("status")) {
                    status = (int) map.get("status");
                }
            }
        } catch (Exception ignored) {}

        errors.put("error", userMessage);
        return ResponseEntity.status(status).body(errors);
    }


    @Test
    void handleFeignException_withMalformedJson_returnsDefaultMessage() {
        String brokenJson = "error sin formato";

        FeignException ex = mock(FeignException.class);
        when(ex.contentUTF8()).thenReturn(brokenJson);

        ResponseEntity<Map<String, String>> response = handler.handleFeignException(ex);

        assertEquals(503, response.getStatusCodeValue());
        assertEquals("Servicio de usuarios no disponible", response.getBody().get("error"));
    }
}
