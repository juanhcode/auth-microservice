package com.develop.auth_microservice.application.use_cases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.develop.auth_microservice.domain.models.Auth;
import com.develop.auth_microservice.infrastructure.clients.UsersClientRest;
import com.develop.auth_microservice.infrastructure.clients.models.Role;
import com.develop.auth_microservice.infrastructure.clients.models.Users;
import com.develop.auth_microservice.infrastructure.repositories.AuthRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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

    @Mock
    private UsersClientRest usersClientRest;

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
    }
    @Test
    void authenticate_ShouldReturnTokenWithRole() {
        // Arrange
        Auth auth = new Auth();
        auth.setEmail("test@example.com");
        auth.setSalt("salt");
        auth.setPassword("hashedpass");

        when(authRepository.findByEmail("test@example.com")).thenReturn(Optional.of(auth));
        when(pbkdf2Service.verifyHash("validpass", "salt", "hashedpass")).thenReturn(true);

        // Mock para el token de servicio
        when(jwtService.generateToken("service-user", "default-role"))
                .thenReturn("service-token");

        // Mock para el usuario y su rol
        Role role = new Role();
        role.setName("ROLE_USER");
        Users user = new Users();
        user.setRole(role);
        List<Users> usersList = List.of(user);

        when(usersClientRest.getUser(eq("Bearer service-token"), anyMap()))
                .thenReturn(usersList);

        // Mock para el token final
        when(jwtService.generateToken("test@example.com", "ROLE_USER"))
                .thenReturn("fake.jwt.token.with.role");

        // Act
        String result = authService.authenticate("test@example.com", "validpass");

        // Assert
        assertEquals("fake.jwt.token.with.role", result);
        verify(jwtService).generateToken("test@example.com", "ROLE_USER");
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
        verify(jwtService, never()).generateToken(anyString(), anyString());
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
        verify(jwtService, never()).generateToken(anyString(), String.valueOf(anyInt()));
    }
}
