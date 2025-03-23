package com.develop.auth_microservice.presentation.controllers;


import com.develop.auth_microservice.application.dtos.LoginRequest;
import com.develop.auth_microservice.domain.interfaces.AuthService;
import com.develop.auth_microservice.domain.models.Auth;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService; // Inyecta la interfaz AuthService

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody Auth auth) {
        authService.register(auth);
        return ResponseEntity.ok("Usuario registrado exitosamente");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        boolean isAuthenticated = authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        if (isAuthenticated) {
            return ResponseEntity.ok("Autenticación exitosa");
        } else {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }
    }
}