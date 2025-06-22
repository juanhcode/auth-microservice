package com.develop.auth_microservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AuthMicroserviceApplication {

	public static void main(String[] args) {
		System.out.println("Starting Auth Microservice...");
		SpringApplication.run(AuthMicroserviceApplication.class, args);
	}

}
