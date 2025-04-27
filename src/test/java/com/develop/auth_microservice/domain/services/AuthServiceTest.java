package com.develop.auth_microservice.domain.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.develop.auth_microservice.domain.interfaces.AuthService;
import com.develop.auth_microservice.domain.interfaces.Pbkdf2Service;
import com.develop.auth_microservice.domain.models.Auth;
import com.develop.auth_microservice.domain.repositories.AuthRepositoryMock;

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

    @InjectMocks
    private AuthService authService = new AuthService() {
        // Implementación inline para pruebas
        @Override
        public void register(Auth auth) {
            String salt = pbkdf2Service.generateSalt();
            String hashedPassword = pbkdf2Service.generateHash(auth.getPassword(), salt);
            auth.setPassword(hashedPassword);
            auth.setSalt(salt);
            authRepository.save(auth);
        }

        @Override
        public boolean authenticate(String email, String password) {
            Auth auth = authRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
            return pbkdf2Service.verifyHash(password, auth.getSalt(), auth.getPassword());
        }
    };

    @Test
    void register_ShouldHashPassword() {
        // Configuración de mocks
        when(pbkdf2Service.generateSalt()).thenReturn("somesalt");
        when(pbkdf2Service.generateHash("plainpass", "somesalt")).thenReturn("hashedpass");

        Auth user = new Auth();
        user.setEmail("test@example.com");
        user.setPassword("plainpass");

        // Ejecución
        authService.register(user);

        // Verificaciones
        verify(pbkdf2Service).generateSalt();
        verify(pbkdf2Service).generateHash("plainpass", "somesalt");
        verify(authRepository).save(user);
        assertEquals("hashedpass", user.getPassword());
        assertEquals("somesalt", user.getSalt());
    }

    @Test
    void authenticate_ShouldReturnTrueForValidCredentials() {
        // Configuración
        Auth storedUser = new Auth();
        storedUser.setSalt("somesalt");
        storedUser.setPassword("hashedpass");

        when(authRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(storedUser));
        when(pbkdf2Service.verifyHash("validpass", "somesalt", "hashedpass"))
            .thenReturn(true);

        // Ejecución y verificación
        assertTrue(authService.authenticate("test@example.com", "validpass"));
    }
}   