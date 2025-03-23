package com.develop.auth_microservice.presentation.controller;

import com.develop.auth_microservice.domain.model.TestEntity;
import com.develop.auth_microservice.domain.repository.TestRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private TestRepository repository;

    @PostMapping
    public TestEntity save(@RequestBody TestEntity testEntity) {
        return repository.save(testEntity);
    }

    @GetMapping
    public List<TestEntity> getAll() {
        return repository.findAll();
    }
}