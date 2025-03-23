package com.develop.auth_microservice.domain.services;

import com.develop.auth_microservice.domain.models.Auth;
import com.develop.auth_microservice.domain.repositories.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService { // Implementa la interfaz AuthService

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private Pbkdf2ServiceImpl pbkdf2Service;

    @Override
    public void register(Auth auth) {
        String salt = pbkdf2Service.generateSalt(); // Genera un salt aleatorio
        String hashedPassword = pbkdf2Service.generateHash(auth.getPassword(), salt); // Genera el hash
        auth.setPassword(hashedPassword);
        auth.setSalt(salt);
        authRepository.save(auth);
    }

    @Override
    public boolean authenticate(String email, String password) {
        Auth auth = authRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return pbkdf2Service.verifyHash(password, auth.getSalt(), auth.getPassword());
    }
}