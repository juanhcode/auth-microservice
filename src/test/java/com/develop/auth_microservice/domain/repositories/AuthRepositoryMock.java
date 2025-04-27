package com.develop.auth_microservice.domain.repositories;

import com.develop.auth_microservice.domain.models.Auth;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AuthRepositoryMock {
    private final Map<String, Auth> users = new HashMap<>();

    public Optional<Auth> findByEmail(String email) {
        return Optional.ofNullable(users.get(email));
    }

    public void save(Auth user) {
        users.put(user.getEmail(), user);
    }

    // Para limpieza entre tests
    public void clear() {
        users.clear();
    }
}