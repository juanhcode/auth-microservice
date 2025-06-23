package com.develop.auth_microservice.application.dtos;

import com.develop.auth_microservice.domain.models.Auth;
import com.develop.auth_microservice.infrastructure.clients.models.Users;
import lombok.Data;

@Data
public class RegisterRequest {
    private Auth auth;
    private Users user;
}
