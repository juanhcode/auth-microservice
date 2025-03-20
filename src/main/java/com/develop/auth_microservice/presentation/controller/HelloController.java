package com.develop.auth_microservice.presentation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping()
    public String sayHello() {
        return "¡Hola Mundo desde el endpoint prueba de Spring Boot! 🚀";
    }
}