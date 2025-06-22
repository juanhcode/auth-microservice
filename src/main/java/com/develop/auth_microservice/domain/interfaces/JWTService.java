package com.develop.auth_microservice.domain.interfaces;

import com.develop.auth_microservice.domain.models.Auth;
import io.jsonwebtoken.Claims;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

public interface JWTService {
    String extractEmail(String token); // Método para extraer el correo electrónico del token
    String generateToken(String email, Integer role); // Método para generar un token
    boolean validateToken(String token, String email); // Método para validar el token
    Claims extractAllClaims(String token); // Método para extraer todas las reclamaciones del token
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver); // Método para extraer una reclamación específica del token
    boolean isTokenExpired(String token); // Método para verificar si el token ha expirado
    Date extractExpiration(String token); // Método para extraer la fecha de expiración del token
    SecretKey getKey(); // Método para obtener la clave secreta
}
