package com.driveeasy.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configures Swagger UI with:
 *  - API metadata (title, version, description)
 *  - "Authorize" button that sends Bearer tokens on all requests
 *
 * Access at: http://localhost:8080/swagger-ui.html
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "DriveEasy Rentals API",
                version = "3.0",
                description = "REST API for the DriveEasy car rental platform. " +
                        "Login at /api/v1/auth/login to get a JWT token, " +
                        "then click Authorize and paste: Bearer <your-token>"
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
    // Annotations do all the work — no bean methods needed
}