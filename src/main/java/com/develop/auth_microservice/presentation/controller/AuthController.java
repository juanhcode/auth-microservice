package com.develop.auth_microservice.presentation.controller;


import com.develop.auth_microservice.application.dto.LoginRequest;
import com.develop.auth_microservice.domain.model.Auth;
import com.develop.auth_microservice.application.use_cases.AuthService;

import jakarta.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Validated // Habilita la validación a nivel de método
public class AuthController {

    @Autowired
    private AuthService authService;

    // Endpoint de autenticación
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        boolean isAuthenticated = authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        if (isAuthenticated) {
            return ResponseEntity.ok("Autenticación exitosa");
        } else {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }
    }


    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody Auth auth) {
        authService.register(auth);
        return ResponseEntity.ok("Usuario registrado exitosamente");
    }
}
