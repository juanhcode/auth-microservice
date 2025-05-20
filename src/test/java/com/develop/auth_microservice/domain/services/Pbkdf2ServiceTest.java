package com.develop.auth_microservice.domain.services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.develop.auth_microservice.application.use_cases.Pbkdf2ServiceImpl;

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
}