package com.finance.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title       = "Finance Data Processing API",
                version     = "1.0",
                description = "Backend for a finance dashboard with role-based access control",
                contact     = @Contact(name = "Finance Team")
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name   = "bearerAuth",
        type   = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in     = SecuritySchemeIn.HEADER
)
public class OpenApiConfig { }


