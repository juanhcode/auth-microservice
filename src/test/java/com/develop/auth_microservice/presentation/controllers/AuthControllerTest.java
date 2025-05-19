package com.develop.auth_microservice.presentation.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.develop.auth_microservice.domain.interfaces.AuthService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest

@TestPropertySource(properties = {
        "server.port=8080", // Añade esto
        "spring.datasource.url=jdbc:postgresql://ep-dark-mouse-a4irs4kd-pooler.us-east-1.aws.neon.tech/trackitdb",
        "spring.datasource.username=trackitdb_owner",
        "spring.datasource.password=npg_QzrIC3mx1wiE",
        "jwt.secret.key=It-UDfOa3Fohvjk7xHzbER1wLUoAz0i5p4_zn-YxvD0"
})
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
        when(authService.authenticate("cifu123@gmail.com", "123456"))
            .thenReturn(mockToken); // Devuelve String, no boolean

        // Ejecución y verificación
        mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content("{\"email\":\"cifu123@gmail.com\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(mockToken)); // Verifica el token retornado
    }

    @Test
void login_ShouldReturn401_WhenCredentialsInvalid() throws Exception {
    when(authService.authenticate("cifu123@gmail.com", "wrongPass"))
        .thenReturn("Error");

    mockMvc.perform(post("/auth/login")
            .contentType("application/json")
            .content("{\"email\":\"cifu123@gmail.com\",\"password\":\"wrongPass\"}"))
            .andExpect(status().isUnauthorized()) // Espera 401
            .andExpect(content().string("Credenciales incorrectas")); // Verifica el mensaje
}
}