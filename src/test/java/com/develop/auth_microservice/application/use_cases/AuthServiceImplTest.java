package com.develop.auth_microservice.application.use_cases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.develop.auth_microservice.domain.models.Auth;
import com.develop.auth_microservice.infrastructure.repositories.AuthRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private Pbkdf2ServiceImpl pbkdf2Service;

    @Mock
    private JWTServiceImpl jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        // No necesitamos configuración adicional ya que usamos @Mock y @InjectMocks
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
    }    @Test
    void authenticate_ShouldReturnTokenWithRole() {
        // Arrange
        Auth auth = new Auth();
        auth.setEmail("test@example.com");
        auth.setSalt("salt");
        auth.setPassword("hashedpass");
        
        when(authRepository.findByEmail("test@example.com")).thenReturn(Optional.of(auth));
        when(pbkdf2Service.verifyHash("validpass", "salt", "hashedpass")).thenReturn(true);
        
        // Configuramos el mock para el jwtService con un valor nulo para el roleId
        // ya que en la implementación se está usando un Users recién creado con roleId = null
        when(jwtService.generateToken(eq("test@example.com"), isNull()))
            .thenReturn("fake.jwt.token.with.role");

        // Act
        String result = authService.authenticate("test@example.com", "validpass");

        // Assert
        assertEquals("fake.jwt.token.with.role", result);
        // Verificamos que se llama al generateToken con un valor null de rol
        verify(jwtService).generateToken(eq("test@example.com"), isNull());
    }

    @Test
    void authenticate_ShouldReturnErrorWhenInvalidPassword() {
        // Arrange
        Auth auth = new Auth();
        auth.setEmail("test@example.com");
        auth.setSalt("salt");
        auth.setPassword("hashedpass");
        
        when(authRepository.findByEmail("test@example.com")).thenReturn(Optional.of(auth));
        when(pbkdf2Service.verifyHash("wrongpass", "salt", "hashedpass")).thenReturn(false);

        // Act
        String result = authService.authenticate("test@example.com", "wrongpass");

        // Assert
        assertEquals("Error", result);
        verify(jwtService, never()).generateToken(anyString(), anyInt());
    }

    @Test
    void authenticate_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(authRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.authenticate("nonexistent@example.com", "anypass");
        });
        
        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(jwtService, never()).generateToken(anyString(), anyInt());
    }
}
