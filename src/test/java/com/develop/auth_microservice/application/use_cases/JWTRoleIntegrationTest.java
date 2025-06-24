package com.develop.auth_microservice.application.use_cases;

import static org.junit.jupiter.api.Assertions.*;

import com.develop.auth_microservice.domain.interfaces.JWTService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class JWTRoleIntegrationTest {

    private JWTService jwtService;
    private static final String TEST_EMAIL = "test@example.com";
    private static final Integer TEST_ROLE_USER = 1;
    private static final Integer TEST_ROLE_ADMIN = 2;
    private static final String TEST_SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    @BeforeEach
    void setUp() {
        jwtService = new JWTServiceImpl();
        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET_KEY);
    }

    @Test
    void testTokenIncludesRole_User() {
        // Generar token con rol de usuario
        String token = jwtService.generateToken(TEST_EMAIL, String.valueOf(TEST_ROLE_USER));

        // Extraer claims y verificar el rol
        Claims claims = jwtService.extractAllClaims(token);
        assertEquals(String.valueOf(TEST_ROLE_USER), claims.get("role", String.class),
                "El token debe incluir el rol de usuario correcto");
    }

    @Test
    void testTokenIncludesRole_Admin() {
        // Generar token con rol de administrador
        String token = jwtService.generateToken(TEST_EMAIL, String.valueOf(TEST_ROLE_ADMIN));

        // Extraer claims y verificar el rol
        Claims claims = jwtService.extractAllClaims(token);
        assertEquals(String.valueOf(TEST_ROLE_ADMIN), claims.get("role", String.class),
                "El token debe incluir el rol de administrador correcto");
    }

    @Test
    void testTokenIncludesRole_DifferentTokensForDifferentRoles() {
        // Generar tokens con roles diferentes
        String tokenUser = jwtService.generateToken(TEST_EMAIL, String.valueOf(TEST_ROLE_USER));
        String tokenAdmin = jwtService.generateToken(TEST_EMAIL, String.valueOf(TEST_ROLE_ADMIN));

        // Verificar que los tokens son diferentes
        assertNotEquals(tokenUser, tokenAdmin,
                "Los tokens para diferentes roles deben ser diferentes");

        // Verificar que ambos tienen el email correcto
        assertEquals(TEST_EMAIL, jwtService.extractEmail(tokenUser));
        assertEquals(TEST_EMAIL, jwtService.extractEmail(tokenAdmin));

        // Verificar que los roles extraídos son diferentes
        Claims userClaims = jwtService.extractAllClaims(tokenUser);
        Claims adminClaims = jwtService.extractAllClaims(tokenAdmin);

        assertEquals(String.valueOf(TEST_ROLE_USER), userClaims.get("role", String.class));
        assertEquals(String.valueOf(TEST_ROLE_ADMIN), adminClaims.get("role", String.class));
    }
}
