package com.develop.auth_microservice.domain.interfaces;

import java.security.Key;

public interface JWTService {
    String generateToken(String email); // Método para generar un token
    Key getKey(); // Método para obtener la clave secreta
}
