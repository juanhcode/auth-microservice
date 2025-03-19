package com.develop.auth_microservice.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "test_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}