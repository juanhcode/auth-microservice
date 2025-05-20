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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.develop.auth_microservice.application.dtos.LoginRequest;
import com.develop.auth_microservice.domain.interfaces.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void login_ShouldReturn200AndToken_WhenCredentialsValid() throws Exception {
        // Configuración del mock
        String mockToken = "fake.jwt.token";
        when(authService.authenticate("cifu123@gmail.com", "123456"))
                .thenReturn(mockToken);

        // Ejecución y verificación
        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"cifu123@gmail.com\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(mockToken));
    }

    @Test
    void login_ShouldReturn401_WhenCredentialsInvalid() throws Exception {
        when(authService.authenticate("cifu123@gmail.com", "wrongPass"))
                .thenReturn("Error");

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"cifu123@gmail.com\",\"password\":\"wrongPass\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Credenciales incorrectas"));
    }
}