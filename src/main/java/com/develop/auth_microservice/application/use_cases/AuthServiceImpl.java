package com.develop.auth_microservice.application.use_cases;

import com.develop.auth_microservice.domain.interfaces.AuthService;
import com.develop.auth_microservice.domain.models.Auth;
import com.develop.auth_microservice.infrastructure.clients.UsersClientRest;
import com.develop.auth_microservice.infrastructure.clients.models.Users;
import com.develop.auth_microservice.infrastructure.repositories.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService { // Implementa la interfaz AuthService

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private Pbkdf2ServiceImpl pbkdf2Service;

    @Autowired
    private JWTServiceImpl jwtService;

    @Autowired
    private UsersClientRest usersClientRest;

    @Override
    public void register(Auth auth) {
        String salt = pbkdf2Service.generateSalt(); // Genera un salt aleatorio
        String hashedPassword = pbkdf2Service.generateHash(auth.getPassword(), salt); // Genera el hash
        auth.setPassword(hashedPassword);
        auth.setSalt(salt);
        authRepository.save(auth);
    }

    @Override
    public String authenticate(String email, String password) {
        Auth auth = authRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (pbkdf2Service.verifyHash(password, auth.getSalt(), auth.getPassword())) {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("email", email);
            String serviceToken = jwtService.generateToken("service-user", "default-role"); // Genera un token de servicio
            // Obtenemos el rol del usuario
            List<Users> users = usersClientRest.getUser("Bearer " +  serviceToken, requestBody);
            Users user = users.get(0);
            System.out.println("Usuario obtenido: " + users);
            return jwtService.generateToken(email, user.getRole().getName());
        }
        return "Error";
    }
}