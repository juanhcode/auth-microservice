package com.develop.auth_microservice.infrastructure.clients;

import com.develop.auth_microservice.application.dtos.RegisterRequest;
import com.develop.auth_microservice.infrastructure.clients.models.Users;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "users-microservice", url = "${feign.client.url}")
public interface UsersClientRest {
    @PostMapping("/users/create")
    Users createdUser(@RequestBody Users registerRequest);
}
