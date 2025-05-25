package com.develop.auth_microservice.application.dtos;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void loginRequest_ShouldValidateConstraints() {
        LoginRequest request = new LoginRequest();
        request.setEmail("invalid-email");
        request.setPassword("short");

        var violations = validator.validate(request);
        assertEquals(2, violations.size());
    }

    @Test
    void loginRequest_ShouldAcceptValidData() {
        LoginRequest request = new LoginRequest();
        request.setEmail("valid@example.com");
        request.setPassword("validPassword123");

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void loginRequest_ShouldWorkWithLombok() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        assertEquals("test@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
        assertNotNull(request.toString());
    }

    @Test
    void loginRequest_ShouldRejectNullEmail() {
        LoginRequest request = new LoginRequest();
        request.setPassword("validPassword123");

        var violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("email no puede estar vacío")));
    }

    @Test
    void loginRequest_ShouldRejectNullPassword() {
        LoginRequest request = new LoginRequest();
        request.setEmail("valid@example.com");

        var violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("contraseña no puede estar vacía")));
    }

    @Test
    void loginRequest_ShouldValidatePasswordLength() {
        LoginRequest request = new LoginRequest();
        request.setEmail("valid@example.com");
        request.setPassword("12345"); // menos de 6 caracteres

        var violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("contraseña debe tener al menos 6 caracteres")));
    }

    @Test
    void loginRequest_ShouldHandleSpecialCharacters() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test.user+label@example.com");
        request.setPassword("p@$$w0rd!123");

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty(), 
            "Debería aceptar caracteres especiales en email y contraseña");
    }

    @Test
    void loginRequest_ShouldValidateEmailFormat() {
        LoginRequest request = new LoginRequest();
        request.setEmail("not.an.email");
        request.setPassword("validPassword123");

        var violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("email debe tener un formato válido")));
    }
}