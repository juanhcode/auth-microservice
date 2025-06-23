package com.develop.auth_microservice.infrastructure.clients.models;

import lombok.Data;

@Data
public class Users {
    private Integer id;
    private String name;
    private String lastName;
    private String email;
    private String address;
    private boolean enabled;
    private Role role;
}
