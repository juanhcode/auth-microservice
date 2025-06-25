package com.develop.auth_microservice.application.use_cases;

import com.develop.auth_microservice.infrastructure.DatabaseUrlLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseUrlLoggerTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private DatabaseMetaData metaData;

    private DatabaseUrlLogger logger;

    @BeforeEach
    void setUp() {
        logger = new DatabaseUrlLogger();
        logger.dataSource = dataSource; // inyectamos el mock directamente
    }

    @Test
    void printDatabaseUrl_shouldPrintUrl_whenConnectionSuccessful() throws Exception {
        // Given
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(metaData);
        when(metaData.getURL()).thenReturn("jdbc:h2:mem:test");

        // When
        logger.printDatabaseUrl();

        // Then: se espera que no lance excepción
        verify(dataSource).getConnection();
        verify(connection).close();
    }

    @Test
    void printDatabaseUrl_shouldHandleException_whenConnectionFails() throws Exception {
        // Given
        when(dataSource.getConnection()).thenThrow(new RuntimeException("DB down"));

        // When
        logger.printDatabaseUrl();

        // Then: no se lanza excepción, solo se imprime stack trace
        verify(dataSource).getConnection();
    }
}

