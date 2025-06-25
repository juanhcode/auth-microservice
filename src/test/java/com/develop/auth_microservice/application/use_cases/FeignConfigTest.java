package com.develop.auth_microservice.application.use_cases;

import com.develop.auth_microservice.infrastructure.configurations.FeignConfig;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeignConfigTest {

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private RequestTemplate requestTemplate;

    private FeignConfig feignConfig;

    @BeforeEach
    void setUp() {
        feignConfig = new FeignConfig();
    }

    @Test
    void apply_shouldAddAuthorizationHeader_ifPresent() {
        // Given
        String expectedToken = "Bearer abc123";
        Vector<String> headers = new Vector<>();
        headers.add("Authorization");

        when(mockRequest.getHeaderNames()).thenReturn(headers.elements());
        when(mockRequest.getHeader("Authorization")).thenReturn(expectedToken);

        ServletRequestAttributes attributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(attributes);

        // When
        feignConfig.apply(requestTemplate);

        // Then
        verify(requestTemplate).header("Authorization", expectedToken);

        // Limpieza
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void apply_shouldNotAddHeader_ifAuthorizationNotPresent() {
        // Given
        Vector<String> headers = new Vector<>();
        headers.add("Content-Type"); // no Authorization

        when(mockRequest.getHeaderNames()).thenReturn(headers.elements());

        ServletRequestAttributes attributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(attributes);

        // When
        feignConfig.apply(requestTemplate);

        // Then
        verify(requestTemplate, never()).header(eq("Authorization"), anyString());

        // Limpieza
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void apply_shouldNotFail_ifAttributesAreNull() {
        // Given
        RequestContextHolder.resetRequestAttributes(); // no request context

        // When / Then
        feignConfig.apply(requestTemplate);

        // No exception debe lanzarse y no se debe llamar a template
        verify(requestTemplate, never()).header(anyString(), anyString());
    }

    @Test
    void apply_shouldNotFail_ifHeaderNamesIsNull() {
        // Given
        when(mockRequest.getHeaderNames()).thenReturn(null);

        ServletRequestAttributes attributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(attributes);

        // When
        feignConfig.apply(requestTemplate);

        // Then
        verify(requestTemplate, never()).header(anyString(), anyString());

        // Limpieza
        RequestContextHolder.resetRequestAttributes();
    }
}
