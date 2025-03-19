package com.develop.auth_microservice.domain.repository;

import com.develop.auth_microservice.domain.model.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    Optional<Auth> findByEmail(String email);
}