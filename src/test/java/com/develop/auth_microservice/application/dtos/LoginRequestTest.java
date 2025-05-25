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
}