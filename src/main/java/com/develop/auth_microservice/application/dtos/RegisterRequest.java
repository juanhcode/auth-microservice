package com.develop.auth_microservice.application.dtos;

import com.develop.auth_microservice.domain.models.Auth;
import com.develop.auth_microservice.infrastructure.clients.models.Users;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class RegisterRequest {
    private Auth auth;
    private Users user;
}
