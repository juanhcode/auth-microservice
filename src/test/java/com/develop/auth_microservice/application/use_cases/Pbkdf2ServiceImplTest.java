package com.develop.auth_microservice.application.use_cases;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;

@ExtendWith(MockitoExtension.class)
public class Pbkdf2ServiceImplTest {

    @InjectMocks
    private Pbkdf2ServiceImpl pbkdf2Service;

    private static final String TEST_PASSWORD = "miContraseña123";

    @Test
    void generateSalt_ShouldReturnValidSalt() {
        // Generar salt
        String salt = pbkdf2Service.generateSalt();
        
        // Verificar que el salt no sea nulo
        assertNotNull(salt, "El salt no debería ser nulo");
        
        // Verificar que el salt sea una cadena Base64 válida
        assertDoesNotThrow(() -> Base64.getDecoder().decode(salt),
            "El salt debería ser una cadena Base64 válida");
        
        // Verificar que el salt tenga la longitud correcta (16 bytes = 24 caracteres en Base64)
        assertEquals(24, salt.length(),
            "El salt debería tener una longitud de 24 caracteres en Base64");
    }

    @Test
    void generateHash_ShouldReturnConsistentHash() {
        String salt = pbkdf2Service.generateSalt();
        String hash1 = pbkdf2Service.generateHash(TEST_PASSWORD, salt);
        String hash2 = pbkdf2Service.generateHash(TEST_PASSWORD, salt);
        
        assertNotNull(hash1, "El hash no debería ser nulo");
        assertEquals(hash1, hash2,
            "Los hashes generados con la misma contraseña y salt deberían ser idénticos");
    }

    @Test
    void generateHash_ShouldReturnDifferentHashesWithDifferentSalts() {
        String salt1 = pbkdf2Service.generateSalt();
        String salt2 = pbkdf2Service.generateSalt();
        
        String hash1 = pbkdf2Service.generateHash(TEST_PASSWORD, salt1);
        String hash2 = pbkdf2Service.generateHash(TEST_PASSWORD, salt2);
        
        assertNotEquals(hash1, hash2,
            "Los hashes generados con diferentes salts deberían ser diferentes");
    }

    @Test
    void verifyHash_ShouldReturnTrueForValidPassword() {
        String salt = pbkdf2Service.generateSalt();
        String hash = pbkdf2Service.generateHash(TEST_PASSWORD, salt);
        
        assertTrue(pbkdf2Service.verifyHash(TEST_PASSWORD, salt, hash),
            "La verificación debería ser exitosa para la contraseña correcta");
    }

    @Test
    void verifyHash_ShouldReturnFalseForInvalidPassword() {
        String salt = pbkdf2Service.generateSalt();
        String hash = pbkdf2Service.generateHash(TEST_PASSWORD, salt);
        
        assertFalse(pbkdf2Service.verifyHash("contraseñaIncorrecta", salt, hash),
            "La verificación debería fallar para una contraseña incorrecta");
    }

    @Test
    void generateHash_ShouldHandleEmptyPassword() {
        String salt = pbkdf2Service.generateSalt();
        assertDoesNotThrow(() -> pbkdf2Service.generateHash("", salt),
            "El servicio debería manejar contraseñas vacías sin lanzar excepciones");
    }

    @Test
    void generateHash_ShouldHandleLongPassword() {
        String salt = pbkdf2Service.generateSalt();
        String longPassword = "a".repeat(1000);
        
        assertDoesNotThrow(() -> {
            String hash = pbkdf2Service.generateHash(longPassword, salt);
            assertNotNull(hash, "El hash de una contraseña larga no debería ser nulo");
        }, "El servicio debería manejar contraseñas largas sin lanzar excepciones");
    }

    @Test
    void generateHash_ShouldHandleSpecialCharacters() {
        String salt = pbkdf2Service.generateSalt();
        String specialPassword = "!@#$%^&*()_+{}[]|\\:;\"'<>,.?/~`áéíóúñÑ";
        
        assertDoesNotThrow(() -> {
            String hash = pbkdf2Service.generateHash(specialPassword, salt);
            assertNotNull(hash, "El hash de una contraseña con caracteres especiales no debería ser nulo");
            assertTrue(pbkdf2Service.verifyHash(specialPassword, salt, hash),
                "La verificación debería funcionar con caracteres especiales");
        }, "El servicio debería manejar caracteres especiales sin lanzar excepciones");
    }


}
