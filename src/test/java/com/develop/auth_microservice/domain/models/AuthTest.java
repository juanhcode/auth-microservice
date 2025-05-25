package com.develop.auth_microservice.domain.models;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class AuthTest {
    
    private Validator validator;
    private Auth auth;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        auth = new Auth();
    }

    @Test
    void testValidAuth() {
        auth.setEmail("test@example.com");
        auth.setPassword("password123");
        auth.setRol("USER");
        auth.setSalt("randomSalt");

        var violations = validator.validate(auth);
        assertTrue(violations.isEmpty(), "No deberían haber violaciones de validación");
    }

    @Test
    void testInvalidEmail() {
        auth.setEmail("invalid-email");
        auth.setPassword("password123");

        var violations = validator.validate(auth);
        assertFalse(violations.isEmpty(), "Debería haber violaciones de validación");
        assertEquals(1, violations.size(), "Debería haber una violación");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("email debe tener un formato válido")));
    }

    @Test
    void testEmptyEmail() {
        auth.setEmail("");
        auth.setPassword("password123");

        var violations = validator.validate(auth);
        assertFalse(violations.isEmpty(), "Debería haber violaciones de validación");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("email no puede estar vacío")));
    }

    @Test
    void testShortPassword() {
        auth.setEmail("test@example.com");
        auth.setPassword("12345"); // menos de 6 caracteres

        var violations = validator.validate(auth);
        assertFalse(violations.isEmpty(), "Debería haber violaciones de validación");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("contraseña debe tener al menos 6 caracteres")));
    }

    @Test
    void testEmptyPassword() {
        auth.setEmail("test@example.com");
        auth.setPassword("");

        var violations = validator.validate(auth);
        assertFalse(violations.isEmpty(), "Debería haber violaciones de validación");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("contraseña no puede estar vacía")));
    }

    @Test
    void testIdUserGetterAndSetter() {
        Long idUser = 1L;
        auth.setIdUser(idUser);
        assertEquals(idUser, auth.getIdUser(), "El ID del usuario debería coincidir");
    }

    @Test
    void testRolGetterAndSetter() {
        String rol = "ADMIN";
        auth.setRol(rol);
        assertEquals(rol, auth.getRol(), "El rol debería coincidir");
    }

    @Test
    void testSaltGetterAndSetter() {
        String salt = "randomSaltValue";
        auth.setSalt(salt);
        assertEquals(salt, auth.getSalt(), "El salt debería coincidir");
    }

    @Test
    void testToString() {
        auth.setEmail("test@example.com");
        auth.setPassword("password123");
        auth.setRol("USER");
        auth.setSalt("salt123");

        String toString = auth.toString();
        
        assertTrue(toString.contains("test@example.com"), "ToString debería contener el email");
        assertTrue(toString.contains("password123"), "ToString debería contener la contraseña");
        assertTrue(toString.contains("USER"), "ToString debería contener el rol");
        assertTrue(toString.contains("salt123"), "ToString debería contener el salt");
    }

    @Test
    void testNullValues() {
        var violations = validator.validate(auth);
        assertEquals(2, violations.size(), "Deberían haber 2 violaciones (email y password requeridos)");
    }
}
