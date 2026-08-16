package com.agro.trace.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private int port;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Agro Trace - Blockchain Agricultural Supply Chain API")
                        .description("""
                                Government-grade backend for blockchain-based agricultural food supply chain traceability.
                                
                                This API supports the complete supply chain lifecycle:
                                - Farmer lot creation and management
                                - Collection agent operations
                                - Nodal center package splitting
                                - Supplier routing and custody
                                - Quality testing with FSSAI standards
                                - Manufacturing lot merging
                                - Bundle creation and distribution
                                - Retailer receipt and consumer verification
                                - Government investigation workflows
                                - QR-based traceability
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Agro Trace Team")
                                .email("support@agrotrace.gov.in"))
                        .license(new License()
                                .name("Government of India")
                                .url("https://www.fssai.gov.in")))
                .servers(List.of(
                        new Server().url("http://localhost:" + port + "/api/v1").description("Local Development"),
                        new Server().url("https://api.agrotrace.gov.in/api/v1").description("Production")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT Access Token (obtained from /auth/*/login)")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}