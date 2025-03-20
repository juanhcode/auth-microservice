package com.develop.auth_microservice.application.use_cases;

import com.develop.auth_microservice.domain.model.Auth;
import com.develop.auth_microservice.domain.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class AuthService {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private Pbkdf2Service pbkdf2Service;

    // Almacena la contraseña encriptada
    public void register(Auth auth) {
        String salt = pbkdf2Service.generateSalt(); // Genera un salt aleatorio
        String hashedPassword = pbkdf2Service.generateHash(auth.getPassword(), salt); // Genera el hash
        auth.setPassword(hashedPassword);
        auth.setSalt(salt);
        authRepository.save(auth);
    }

    // Verifica la contraseña durante la autenticación
    public boolean authenticate(String email, String password) {
        Auth auth = authRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return pbkdf2Service.verifyHash(password, auth.getSalt(), auth.getPassword());
    }

    
}