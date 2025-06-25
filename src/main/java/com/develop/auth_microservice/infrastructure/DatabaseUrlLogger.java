package com.develop.auth_microservice.infrastructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class DatabaseUrlLogger {

    @Autowired
    public DataSource dataSource;

    @PostConstruct
    public void printDatabaseUrl() {
        try (Connection connection = dataSource.getConnection()) {
            String url = connection.getMetaData().getURL();
            System.out.println("URL real de la base de datos: " + url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}