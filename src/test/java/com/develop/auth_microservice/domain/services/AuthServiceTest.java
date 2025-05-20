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

    // ===== Tests para register() =====
    @Test
    void register_ShouldHashPasswordAndSaveUser() {
        when(pbkdf2Service.generateSalt()).thenReturn("somesalt");
        when(pbkdf2Service.generateHash("password", "somesalt")).thenReturn("hashedpass");

        Auth user = new Auth();
        user.setPassword("password");
        authService.register(user);

        verify(authRepository).save(user);
        assertEquals("hashedpass", user.getPassword());
        assertEquals("somesalt", user.getSalt());
    }

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

}