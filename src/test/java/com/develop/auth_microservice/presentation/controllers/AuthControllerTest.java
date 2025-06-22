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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.develop.auth_microservice.application.dtos.LoginRequest;
import com.develop.auth_microservice.domain.interfaces.AuthService;
import com.develop.auth_microservice.domain.interfaces.JWTService;
import com.develop.auth_microservice.domain.models.Auth;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private JWTService jwtService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
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
        Auth auth = new Auth();
        auth.setEmail("nuevo@example.com");
        auth.setPassword("password123");
        auth.setRol("USER");

        doNothing().when(authService).register(any(Auth.class));

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(auth)))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario registrado exitosamente"));

        verify(authService).register(any(Auth.class));
    }

    @Test
    void register_ShouldReturn400_WhenEmailInvalid() throws Exception {
        Auth auth = new Auth();
        auth.setEmail("invalid-email");
        auth.setPassword("password123");
        auth.setRol("USER");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(auth)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ShouldReturn400_WhenPasswordTooShort() throws Exception {
        Auth auth = new Auth();
        auth.setEmail("test@example.com");
        auth.setPassword("12345"); // menos de 6 caracteres
        auth.setRol("USER");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(auth)))
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
    }

    @Test
    void register_ShouldReturn400_WhenRequestBodyEmpty() throws Exception {
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validateToken_ShouldReturn400_WhenParamsMissing() throws Exception {
        mockMvc.perform(get("/auth/validate"))
                .andExpect(status().isBadRequest());
    }
}