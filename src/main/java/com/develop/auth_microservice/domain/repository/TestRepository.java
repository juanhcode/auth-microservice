package com.develop.auth_microservice.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.develop.auth_microservice.domain.model.TestEntity;

@Repository
public interface TestRepository extends JpaRepository<TestEntity, Long> {
}
