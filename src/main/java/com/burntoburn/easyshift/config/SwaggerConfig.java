package com.burntoburn.easyshift.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "EasyShift API 문서",
                version = "1.0",
                description = "EasyShift API 문서입니다.",
                contact = @Contact(name = "이열치열", url = "https://github.com/BurnToBurn-Devs")
        ),
        servers = {
                @Server(url = "https://api.easyshift.tech:8443", description = "Development Server"),
                @Server(url = "https://api.easyshift.tech", description = "Production Server"),
                @Server(url = "http://localhost:8080", description = "Local Server"),
        },
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class SwaggerConfig {
    
}
