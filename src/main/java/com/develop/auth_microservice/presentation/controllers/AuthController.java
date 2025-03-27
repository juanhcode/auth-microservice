package com.develop.auth_microservice.presentation.controllers;


import com.develop.auth_microservice.application.dtos.LoginRequest;
import com.develop.auth_microservice.domain.interfaces.AuthService;
import com.develop.auth_microservice.domain.models.Auth;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Lazy
    @Autowired
    private AuthService authService; // Inyecta la interfaz AuthService

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody Auth auth) {
        authService.register(auth);
        return ResponseEntity.ok("Usuario registrado exitosamente");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        String isAuthenticated = authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        if (isAuthenticated.equals("Error")) {
            return ResponseEntity.badRequest().body("Credenciales incorrectas");
        }
        return ResponseEntity.ok(isAuthenticated);
    }
}