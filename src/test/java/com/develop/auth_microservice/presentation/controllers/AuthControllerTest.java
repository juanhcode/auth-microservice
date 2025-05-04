package com.develop.auth_microservice.presentation.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.develop.auth_microservice.domain.interfaces.AuthService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    void login_ShouldReturn200AndToken_WhenCredentialsValid() throws Exception {
        // Configuración del mock para éxito
        String mockToken = "fake.jwt.token";
        when(authService.authenticate("test@example.com", "validPass"))
            .thenReturn(mockToken); // Devuelve String, no boolean

        // Ejecución y verificación
        mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content("{\"email\":\"test@example.com\",\"password\":\"validPass\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(mockToken)); // Verifica el token retornado
    }

    @Test
void login_ShouldReturn401_WhenCredentialsInvalid() throws Exception {
    when(authService.authenticate("test@example.com", "wrongPass"))
        .thenReturn("Error");

    mockMvc.perform(post("/auth/login")
            .contentType("application/json")
            .content("{\"email\":\"test@example.com\",\"password\":\"wrongPass\"}"))
            .andExpect(status().isUnauthorized()) // Espera 401
            .andExpect(content().string("Credenciales incorrectas")); // Verifica el mensaje
}
}