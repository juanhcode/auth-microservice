package com.develop.auth_microservice.domain.services;
import com.develop.auth_microservice.domain.models.Auth;

public interface AuthService {
    void register(Auth auth); // Método para registrar un usuario
    boolean authenticate(String email, String password); // Método para autenticar un usuario
}