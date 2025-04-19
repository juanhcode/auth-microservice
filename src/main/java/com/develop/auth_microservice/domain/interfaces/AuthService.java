package com.develop.auth_microservice.domain.interfaces;
import com.develop.auth_microservice.application.dtos.LoginRequest;
import com.develop.auth_microservice.domain.models.Auth;

import java.security.Key;

public interface AuthService {
    void register(Auth auth); // Método para registrar un usuario
    String authenticate(String email, String password); // Método para autenticar un usuario
}