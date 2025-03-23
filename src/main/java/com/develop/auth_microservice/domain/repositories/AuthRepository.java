package com.develop.auth_microservice.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.develop.auth_microservice.domain.models.Auth;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    Optional<Auth> findByEmail(String email);
}