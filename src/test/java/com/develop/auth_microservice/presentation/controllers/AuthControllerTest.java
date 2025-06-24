package com.develop.auth_microservice.presentation.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.develop.auth_microservice.application.dtos.LoginRequest;
import com.develop.auth_microservice.domain.interfaces.AuthService;
import com.develop.auth_microservice.domain.interfaces.JWTService;
import com.develop.auth_microservice.domain.models.Auth;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private JWTService jwtService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;    @Mock
    private com.develop.auth_microservice.infrastructure.clients.UsersClientRest usersClientRest;    @BeforeEach
    void setUp() {
        // Set the usersClientRest in the controller using reflection
        ReflectionTestUtils.setField(authController, "usersClientRest", usersClientRest);
          
        // Configure MockMvc with validation support and global exception handler
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
            .setControllerAdvice(new com.develop.auth_microservice.presentation.exceptions.GlobalExceptionHandler())
            .setValidator(new org.springframework.validation.beanvalidation.LocalValidatorFactoryBean())
            .build();
            
        objectMapper = new ObjectMapper();
    }

    @Test
    void login_ShouldReturn200AndToken_WhenCredentialsValid() throws Exception {
        // Configuración del mock
        String mockToken = "fake.jwt.token";
        when(authService.authenticate("cifu123@gmail.com", "123456"))
                .thenReturn(mockToken);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("cifu123@gmail.com");
        loginRequest.setPassword("123456");

        // Ejecución y verificación
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(mockToken));

        verify(authService).authenticate("cifu123@gmail.com", "123456");
    }

    @Test
    void login_ShouldReturn401_WhenCredentialsInvalid() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("cifu123@gmail.com");
        loginRequest.setPassword("wrongPass");

        when(authService.authenticate("cifu123@gmail.com", "wrongPass"))
                .thenReturn("Error");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Credenciales incorrectas"));
    }
    @Test
    void register_ShouldReturn200_WhenRegistrationSuccessful() throws Exception {
        // Creamos un objeto Auth
        Auth auth = new Auth();
        auth.setEmail("nuevo@example.com");
        auth.setPassword("password123");

        // Creamos un objeto Users con un id válido
        com.develop.auth_microservice.infrastructure.clients.models.Users user = new com.develop.auth_microservice.infrastructure.clients.models.Users();
        user.setId(123); // ID necesario para el flujo del controlador
        user.setName("Test User");
        user.setLastName("Test LastName");
        user.setEmail("nuevo@example.com");
        user.setAddress("Test Address");
        user.setEnabled(true);
        user.setRoleId(1L);

        // Creamos el RegisterRequest que contiene ambos
        com.develop.auth_microservice.application.dtos.RegisterRequest registerRequest = new com.develop.auth_microservice.application.dtos.RegisterRequest();
        registerRequest.setAuth(auth);
        registerRequest.setUser(user);

        // Mock de los servicios
        when(usersClientRest.createdUser(any(com.develop.auth_microservice.infrastructure.clients.models.Users.class)))
                .thenReturn(user);
        doNothing().when(authService).register(any(Auth.class));

        // Ejecución y verificación
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuario registrado exitosamente"));

        verify(authService).register(any(Auth.class));
        verify(usersClientRest).createdUser(any(com.develop.auth_microservice.infrastructure.clients.models.Users.class));
    }    @Test
    void register_ShouldHandle_WhenServiceThrowsException_InvalidEmail() throws Exception {
        // Crear request con email inválido que cause excepción
        Auth auth = new Auth();
        auth.setEmail("invalid-email");
        auth.setPassword("password123");

        com.develop.auth_microservice.infrastructure.clients.models.Users user = new com.develop.auth_microservice.infrastructure.clients.models.Users();
        user.setEmail("invalid-email");
        user.setRoleId(1L);

        com.develop.auth_microservice.application.dtos.RegisterRequest registerRequest = new com.develop.auth_microservice.application.dtos.RegisterRequest();
        registerRequest.setAuth(auth);
        registerRequest.setUser(user);

        // Mock para lanzar excepción
        doThrow(new IllegalArgumentException("Invalid email format"))
                .when(authService).register(any());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isInternalServerError());
    }@Test
    void register_ShouldReturn400_WhenPasswordTooShort() throws Exception {
        Auth auth = new Auth();
        auth.setEmail("test@example.com");
        auth.setPassword("12345"); // Demasiado corta

        com.develop.auth_microservice.infrastructure.clients.models.Users user = new com.develop.auth_microservice.infrastructure.clients.models.Users();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setRoleId(1L);

        com.develop.auth_microservice.application.dtos.RegisterRequest registerRequest = new com.develop.auth_microservice.application.dtos.RegisterRequest();
        registerRequest.setAuth(auth);
        registerRequest.setUser(user);

        // Mock para que el flujo llegue a authService.register
        when(usersClientRest.createdUser(any())).thenReturn(user);
        doThrow(new IllegalArgumentException("Password too short"))
                .when(authService).register(any());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validateToken_ShouldReturn200_WhenTokenValid() throws Exception {
        String token = "valid.jwt.token";
        String email = "test@example.com";

        when(jwtService.validateToken(token, email)).thenReturn(true);

        mockMvc.perform(get("/auth/validate")
                .param("token", token)
                .param("email", email))
                .andExpect(status().isOk())
                .andExpect(content().string("Token válido"));

        verify(jwtService).validateToken(token, email);
    }

    @Test
    void validateToken_ShouldReturn400_WhenTokenInvalid() throws Exception {
        String token = "invalid.jwt.token";
        String email = "test@example.com";

        when(jwtService.validateToken(token, email)).thenReturn(false);

        mockMvc.perform(get("/auth/validate")
                .param("token", token)
                .param("email", email))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Token inválido"));

        verify(jwtService).validateToken(token, email);
    }

    @Test
    void login_ShouldReturn400_WhenRequestBodyInvalid() throws Exception {
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }    @Test
    void register_ShouldHandleNullPointer_WhenRequestBodyIsEmpty() throws Exception {
        // When an empty request is sent, the controller will likely get a NullPointerException
        // We simulate that situation by making the service throw NPE for any argument
        doThrow(new NullPointerException("Auth cannot be null"))
            .when(authService).register(any());
            
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")) // Empty JSON object
                .andExpect(status().isInternalServerError()); // Most likely response for NPE without specific handler
    }

    @Test
    void validateToken_ShouldReturn400_WhenParamsMissing() throws Exception {
        mockMvc.perform(get("/auth/validate"))
                .andExpect(status().isBadRequest());
    }    @Test
    void login_ShouldReturn400_WhenEmailInvalid() throws Exception {
        // This test checks if the controller returns 400 for invalid email format
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("invalid-email");
        loginRequest.setPassword("password123");

        // If validation is triggered via Bean Validation, it will return 400 BAD_REQUEST
        doThrow(new IllegalArgumentException("Invalid email format"))
            .when(authService).authenticate(anyString(), anyString());

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }    @Test
    void login_ShouldReturn400_WhenPasswordMissing() throws Exception {
        // This test checks if the controller returns 400 for missing password
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword(""); // empty password

        // If validation is triggered via Bean Validation, it will return 400 BAD_REQUEST
        doThrow(new IllegalArgumentException("Password is required"))
            .when(authService).authenticate(anyString(), anyString());

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }
}