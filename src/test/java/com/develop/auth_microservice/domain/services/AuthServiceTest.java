package com.develop.auth_microservice.domain.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.develop.auth_microservice.domain.interfaces.AuthService;
import com.develop.auth_microservice.domain.interfaces.Pbkdf2Service;
import com.develop.auth_microservice.domain.models.Auth;
import com.develop.auth_microservice.domain.repositories.AuthRepositoryMock;
import com.develop.auth_microservice.domain.repositories.JwtService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthRepositoryMock authRepository;

    @Mock
    private Pbkdf2Service pbkdf2Service;

    @Mock
    private JwtService jwtService; // Mock añadido para JWT

    @InjectMocks
    private AuthService authService = new AuthService() {
        @Override
        public void register(Auth auth) {
            String salt = pbkdf2Service.generateSalt();
            String hashedPassword = pbkdf2Service.generateHash(auth.getPassword(), salt);
            auth.setPassword(hashedPassword);
            auth.setSalt(salt);
            authRepository.save(auth);
        }

        @Override
        public String authenticate(String email, String password) {
            Auth auth = authRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            if (pbkdf2Service.verifyHash(password, auth.getSalt(), auth.getPassword())) {
                return jwtService.generateToken(email); // Usa el JwtService mockeado
            }
            return "Error";
        }
    };

    // ===== Tests para authenticate() =====
    @Test
    void authenticate_ShouldReturnTokenWhenCredentialsAreValid() {
        // Configuración del mock
        Auth auth = new Auth();
        auth.setSalt("salt");
        auth.setPassword("hashedpass");
        
        when(authRepository.findByEmail("test@example.com")).thenReturn(Optional.of(auth));
        when(pbkdf2Service.verifyHash("validpass", "salt", "hashedpass")).thenReturn(true);
        when(jwtService.generateToken("test@example.com")).thenReturn("fake.jwt.token"); // Mock de JWT

        // Ejecución
        String result = authService.authenticate("test@example.com", "validpass");

        // Verificación
        assertEquals("fake.jwt.token", result); // Verifica el token retornado
        verify(jwtService).generateToken("test@example.com"); // Verifica que se llamó al JwtService
    }

    @Test
    void authenticate_ShouldReturnErrorWhenPasswordIsInvalid() {
        Auth auth = new Auth();
        auth.setSalt("salt");
        auth.setPassword("hashedpass");
        
        when(authRepository.findByEmail("test@example.com")).thenReturn(Optional.of(auth));
        when(pbkdf2Service.verifyHash("wrongpass", "salt", "hashedpass")).thenReturn(false);

        String result = authService.authenticate("test@example.com", "wrongpass");
        
        assertEquals("Error", result);
        verify(jwtService, never()).generateToken(any()); // Verifica que NO se llamó al JwtService
    }

    @Test
    void authenticate_ShouldThrowExceptionWhenUserNotFound() {
        when(authRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            authService.authenticate("nonexistent@example.com", "anypass");
        });
        
        verify(jwtService, never()).generateToken(any()); // Verifica que NO se llamó al JwtService
    }

    @Test
    void register_ShouldHashPasswordAndSaveUser() {
        // Arrange
        Auth auth = new Auth();
        auth.setPassword("plainPassword");
        
        when(pbkdf2Service.generateSalt()).thenReturn("generatedSalt");
        when(pbkdf2Service.generateHash("plainPassword", "generatedSalt"))
            .thenReturn("hashedPassword");

        // Act
        authService.register(auth);

        // Assert
        verify(pbkdf2Service).generateSalt();
        verify(pbkdf2Service).generateHash("plainPassword", "generatedSalt");
        verify(authRepository).save(auth);
        
        assertEquals("hashedPassword", auth.getPassword());
        assertEquals("generatedSalt", auth.getSalt());
    }

    @Test
    void authenticate_ShouldHandleDatabaseError() {
        when(authRepository.findByEmail("test@example.com"))
            .thenThrow(new RuntimeException("Database error"));
        
        assertThrows(RuntimeException.class, () -> {
            authService.authenticate("test@example.com", "anypass");
        });
    }

    @Test
    void authenticate_ShouldHandleVeryLongPassword() {
        Auth auth = new Auth();
        auth.setSalt("salt");
        auth.setPassword("hashedLongPass");

        String longPassword = "a".repeat(1000);

        when(authRepository.findByEmail("test@example.com")).thenReturn(Optional.of(auth));
        when(pbkdf2Service.verifyHash(longPassword, "salt", "hashedLongPass")).thenReturn(true);
        when(jwtService.generateToken("test@example.com")).thenReturn("token");

        assertDoesNotThrow(() -> {
            authService.authenticate("test@example.com", longPassword);
        });
    }

    @Test
    void authenticate_ShouldHandleSpecialCharacters() {
        Auth auth = new Auth();
        auth.setSalt("salt");
        auth.setPassword("hashedSpecialPass");

        String specialPassword = "p@$$w0rd!áéíóú";

        when(authRepository.findByEmail("test@example.com")).thenReturn(Optional.of(auth));
        when(pbkdf2Service.verifyHash(specialPassword, "salt", "hashedSpecialPass")).thenReturn(true);
        when(jwtService.generateToken("test@example.com")).thenReturn("token");

        assertDoesNotThrow(() -> {
            authService.authenticate("test@example.com", specialPassword);
        });
    }

    @Test
    void register_ShouldNotSaveUserWhenHashingFails() {
        Auth auth = new Auth();
        auth.setPassword("plainPassword");

        when(pbkdf2Service.generateSalt()).thenReturn("salt");
        when(pbkdf2Service.generateHash(anyString(), anyString()))
                .thenThrow(new RuntimeException("Hashing failed"));

        assertThrows(RuntimeException.class, () -> {
            authService.register(auth);
        });

        verify(authRepository, never()).save(any());
    }

    @Test
    void authenticate_ShouldVerifyPasswordExactlyOnce() {
        Auth auth = new Auth();
        auth.setSalt("salt");
        auth.setPassword("hashedpass");

        when(authRepository.findByEmail("test@example.com")).thenReturn(Optional.of(auth));
        when(pbkdf2Service.verifyHash("validpass", "salt", "hashedpass")).thenReturn(true);
        when(jwtService.generateToken("test@example.com")).thenReturn("token");

        authService.authenticate("test@example.com", "validpass");

        verify(pbkdf2Service, times(1))
                .verifyHash("validpass", "salt", "hashedpass");
    }


}