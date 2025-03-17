package com.develop.auth_microservice.domain.service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.springframework.stereotype.Service;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@Service
public class Pbkdf2Service {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 10000; // Número de iteraciones
    private static final int KEY_LENGTH = 256; // Longitud de la clave en bits

    // Genera un hash PBKDF2 de la contraseña
    public String generateHash(String password, String salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error al generar el hash PBKDF2", e);
        }
    }

    // Verifica si la contraseña coincide con el hash almacenado
    public boolean verifyHash(String password, String salt, String storedHash) {
        String generatedHash = generateHash(password, salt);
        return generatedHash.equals(storedHash);
    }

    // Genera un salt aleatorio
    public String generateSalt() {
        byte[] salt = new byte[16];
        new java.security.SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}