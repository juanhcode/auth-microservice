package com.develop.auth_microservice.domain.services;


public interface Pbkdf2Service {
    String generateHash(String password, String salt); // Genera un hash PBKDF2
    boolean verifyHash(String password, String salt, String storedHash); // Verifica un hash
    String generateSalt(); // Genera un salt aleatorio
}