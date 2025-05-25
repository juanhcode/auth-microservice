package com.develop.auth_microservice.infrastructure.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@WebMvcTest(SecurityConfig.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void authenticationManager_ShouldNotBeNull() throws Exception {
        AuthenticationManager authManager = securityConfig.authenticationManagerBean(null);
        assertNotNull(authManager, "El AuthenticationManager no debería ser nulo");
    }


} 