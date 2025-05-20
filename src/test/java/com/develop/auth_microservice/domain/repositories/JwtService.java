package com.develop.auth_microservice.domain.repositories;

public interface JwtService {
    String generateToken(String email);  // Genera un token JWT para el email
}