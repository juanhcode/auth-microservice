package com.develop.auth_microservice.application.use_cases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class JwtServiceImplTest {

    @InjectMocks
    private JWTServiceImpl jwtService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET_KEY);
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        // Generar token
        String token = jwtService.generateToken(TEST_EMAIL);

        // Verificar que el token no sea nulo
        assertNotNull(token, "El token no debería ser nulo");
        
        // Verificar que el token pueda ser parseado
        assertDoesNotThrow(() -> {
            Jwts.parser()
                .verifyWith(jwtService.getKey())
                .build()
                .parseSignedClaims(token);
        }, "El token debería poder ser parseado");

        // Verificar que el email en el token sea correcto
        assertEquals(TEST_EMAIL, jwtService.extractEmail(token), 
            "El email extraído del token debería coincidir con el email original");
    }

    @Test
    void validateToken_ShouldReturnTrueForValidToken() {
        String token = jwtService.generateToken(TEST_EMAIL);
        assertTrue(jwtService.validateToken(token, TEST_EMAIL),
            "El token válido debería ser validado correctamente");
    }

    @Test
    void validateToken_ShouldReturnFalseForInvalidEmail() {
        String token = jwtService.generateToken(TEST_EMAIL);
        assertFalse(jwtService.validateToken(token, "wrong@example.com"),
            "El token debería ser inválido para un email diferente");
    }


    @Test
    void extractAllClaims_ShouldReturnValidClaims() {
        String token = jwtService.generateToken(TEST_EMAIL);
        Claims claims = jwtService.extractAllClaims(token);
        
        assertNotNull(claims, "Los claims no deberían ser nulos");
        assertEquals(TEST_EMAIL, claims.getSubject(), 
            "El subject del claim debería ser el email");
        assertNotNull(claims.getExpiration(), 
            "La fecha de expiración no debería ser nula");
        assertNotNull(claims.getIssuedAt(), 
            "La fecha de emisión no debería ser nula");
    }

    @Test
    void extractEmail_ShouldReturnEmailFromToken() {
        String token = jwtService.generateToken(TEST_EMAIL);
        String extractedEmail = jwtService.extractEmail(token);
        assertEquals(TEST_EMAIL, extractedEmail, 
            "El email extraído debería coincidir con el original");
    }

    @Test
    void extractExpiration_ShouldReturnExpirationDate() {
        String token = jwtService.generateToken(TEST_EMAIL);
        Date expiration = jwtService.extractExpiration(token);
        
        assertNotNull(expiration, "La fecha de expiración no debería ser nula");
        assertTrue(expiration.after(new Date()), 
            "La fecha de expiración debería estar en el futuro");
    }

    @Test
    void isTokenExpired_ShouldReturnFalseForValidToken() {
        String token = jwtService.generateToken(TEST_EMAIL);
        assertFalse(jwtService.isTokenExpired(token),
            "Un token recién generado no debería estar expirado");
    }
}
