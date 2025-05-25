package com.develop.auth_microservice.domain.services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.develop.auth_microservice.application.use_cases.Pbkdf2ServiceImpl;

import java.util.Base64;

class Pbkdf2ServiceTest {

    private Pbkdf2ServiceImpl pbkdf2Service = new Pbkdf2ServiceImpl();

    @Test
    void generateHash_ShouldReturnConsistentResult() {
        String salt = "randomsalt";
        String hash1 = pbkdf2Service.generateHash("mypassword", salt);
        String hash2 = pbkdf2Service.generateHash("mypassword", salt);
        
        assertEquals(hash1, hash2); // Misma entrada -> misma salida
    }

    @Test
    void verifyHash_ShouldReturnTrueForValidPassword() {
        String salt = pbkdf2Service.generateSalt();
        String password = "secure123";
        String hash = pbkdf2Service.generateHash(password, salt);
        
        assertTrue(pbkdf2Service.verifyHash(password, salt, hash));
    }
    @Test
    void generateSalt_ShouldReturnNonNull() {
        String salt = pbkdf2Service.generateSalt();
        assertNotNull(salt);
    }

    @Test
    void generateSalt_ShouldReturnDifferentValues() {
        String salt1 = pbkdf2Service.generateSalt();
        String salt2 = pbkdf2Service.generateSalt();
        assertNotEquals(salt1, salt2); // Deberían ser diferentes
    }

    @Test
    void generateSalt_ShouldReturnBase64Encoded() {
        String salt = pbkdf2Service.generateSalt();
        assertDoesNotThrow(() -> {
            Base64.getDecoder().decode(salt); // Verifica que es Base64 válido
        });
    }

    @Test
    void generateHash_ShouldHandleEmptyPassword() {
        String salt = pbkdf2Service.generateSalt();
        String hash = pbkdf2Service.generateHash("", salt);
        assertNotNull(hash);
        assertTrue(hash.length() > 0);
    }

    @Test
    void generateHash_ShouldHandleLongPassword() {
        String salt = pbkdf2Service.generateSalt();
        String longPassword = "a".repeat(1000); // Contraseña muy larga
        String hash = pbkdf2Service.generateHash(longPassword, salt);
        assertNotNull(hash);
    }

    @Test
    void generateHash_ShouldHandleSpecialCharacters() {
        String salt = pbkdf2Service.generateSalt();
        String password = "p@$$w0rd!áéíóúñÑ";
        String hash = pbkdf2Service.generateHash(password, salt);
        assertNotNull(hash);
    }
    @Test
    void verifyHash_ShouldReturnFalseForInvalidPassword() {
        String salt = pbkdf2Service.generateSalt();
        String hash = pbkdf2Service.generateHash("correctPass", salt);
        assertFalse(pbkdf2Service.verifyHash("wrongPass", salt, hash));
    }

    @Test
    void verifyHash_ShouldReturnFalseForWrongSalt() {
        String salt1 = pbkdf2Service.generateSalt();
        String salt2 = pbkdf2Service.generateSalt();
        String hash = pbkdf2Service.generateHash("password", salt1);
        assertFalse(pbkdf2Service.verifyHash("password", salt2, hash));
    }

    @Test
    void verifyHash_ShouldReturnFalseForEmptyPassword() {
        String salt = pbkdf2Service.generateSalt();
        String hash = pbkdf2Service.generateHash("realPass", salt);
        assertFalse(pbkdf2Service.verifyHash("", salt, hash));
    }

    @Test
    void verifyHash_ShouldReturnFalseForNullHash() {
        String salt = pbkdf2Service.generateSalt();
        assertFalse(pbkdf2Service.verifyHash("password", salt, null));
    }@Test
    void generateHash_ShouldProduceDifferentHashesForSamePasswordDifferentSalts() {
        String salt1 = pbkdf2Service.generateSalt();
        String salt2 = pbkdf2Service.generateSalt();
        String hash1 = pbkdf2Service.generateHash("password", salt1);
        String hash2 = pbkdf2Service.generateHash("password", salt2);
        assertNotEquals(hash1, hash2);
    }

    @Test
    void generateHash_ShouldProduceFixedLengthOutput() {
        String salt = pbkdf2Service.generateSalt();
        String hash1 = pbkdf2Service.generateHash("short", salt);
        String hash2 = pbkdf2Service.generateHash("verylongpassword".repeat(10), salt);
        assertEquals(hash1.length(), hash2.length());
    }
    @Test
    void generatedHash_ShouldNotContainOriginalPassword() {
        String salt = pbkdf2Service.generateSalt();
        String password = "sensitivePassword123";
        String hash = pbkdf2Service.generateHash(password, salt);
        assertFalse(hash.contains(password));
    }

}