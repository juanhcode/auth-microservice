package com.develop.auth_microservice.presentation.controllers;


import com.develop.auth_microservice.application.dtos.LoginRequest;
import com.develop.auth_microservice.application.dtos.RegisterRequest;
import com.develop.auth_microservice.domain.interfaces.AuthService;
import com.develop.auth_microservice.domain.interfaces.JWTService;

import com.develop.auth_microservice.infrastructure.clients.UsersClientRest;
import com.develop.auth_microservice.infrastructure.clients.models.Users;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Lazy
    @Autowired
    private AuthService authService; // Inyecta la interfaz AuthService

    @Lazy
    @Autowired
    private JWTService jwtService; // Inyecta la interfaz JWTService

    @Lazy
    @Autowired
    private UsersClientRest usersClientRest; // Inyecta el cliente REST de usuarios

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        Users createdUser = usersClientRest.createdUser(registerRequest.getUser());
        registerRequest.getAuth().setIdUser(Long.valueOf(createdUser.getId()));
        authService.register(registerRequest.getAuth());
        return ResponseEntity.ok(Collections.singletonMap("message", "Usuario registrado exitosamente"));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest) {
    String isAuthenticated = authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
    if (isAuthenticated.equals("Error")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas"); // Cambiado a 401
    }
    return ResponseEntity.ok(isAuthenticated);
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestParam("token") String token, @RequestParam("email") String email) {
        boolean isValid = jwtService.validateToken(token, email);
        if (!isValid) {
            return ResponseEntity.badRequest().body("Token inválido");
        }
        return ResponseEntity.ok("Token válido");
    }
}