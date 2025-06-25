package com.develop.auth_microservice.application.use_cases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKeyFactory;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class Pbkdf2ServiceImplTest {

    private Pbkdf2ServiceImpl pbkdf2Service;

    @BeforeEach
    void setUp() {
        pbkdf2Service = new Pbkdf2ServiceImpl();
    }

    @Test
    void generateHash_shouldReturnValidBase64String() {
        String password = "securePassword";
        String salt = "customSalt123";

        String hash = pbkdf2Service.generateHash(password, salt);

        assertNotNull(hash);
        assertFalse(hash.isEmpty());

        // Verifica que sea un Base64 válido
        assertDoesNotThrow(() -> Base64.getDecoder().decode(hash));
    }

    @Test
    void verifyHash_shouldReturnTrueForCorrectPassword() {
        String password = "mySecret";
        String salt = "uniqueSalt";

        String hash = pbkdf2Service.generateHash(password, salt);
        boolean result = pbkdf2Service.verifyHash(password, salt, hash);

        assertTrue(result);
    }

    @Test
    void verifyHash_shouldReturnFalseForIncorrectPassword() {
        String password = "correctPassword";
        String wrongPassword = "wrongPassword";
        String salt = "saltValue";

        String hash = pbkdf2Service.generateHash(password, salt);
        boolean result = pbkdf2Service.verifyHash(wrongPassword, salt, hash);

        assertFalse(result);
    }

    @Test
    void generateSalt_shouldReturnValidBase64Salt() {
        String salt = pbkdf2Service.generateSalt();

        assertNotNull(salt);
        assertFalse(salt.isEmpty());

        byte[] decoded = assertDoesNotThrow(() -> Base64.getDecoder().decode(salt));
        assertEquals(16, decoded.length); // ya que es un array de 16 bytes
    }

    @Test
    void generateHash_shouldThrowRuntimeExceptionForInvalidAlgorithm() {
        // Clase anónima para forzar una excepción cambiando el algoritmo
        Pbkdf2ServiceImpl faultyService = new Pbkdf2ServiceImpl() {
            @Override
            public String generateHash(String password, String salt) {
                try {
                    SecretKeyFactory.getInstance("InvalidAlgo");
                    return "should-not-reach";
                } catch (Exception e) {
                    throw new RuntimeException("Error al generar el hash PBKDF2", e);
                }
            }
        };

        assertThrows(RuntimeException.class, () -> faultyService.generateHash("test", "test"));
    }
}

