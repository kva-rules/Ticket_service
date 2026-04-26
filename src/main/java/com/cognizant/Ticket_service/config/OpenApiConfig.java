package com.cognizant.Ticket_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3.0 configuration for Ticket Service.
 *
 * <p>Swagger UI:  /swagger-ui.html</p>
 * <p>API docs:    /v3/api-docs</p>
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ticketServiceOpenAPI() {
        final String schemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Ticket Service API")
                        .version("1.0.0")
                        .description("""
                                Support ticket lifecycle microservice.
                                Handles ticket CRUD, comments, ratings, and engineer assignment.
                                Emits `ticket.created` / `ticket.resolved` events to Kafka for
                                notification-service and reward-service to consume.
                                """)
                        .contact(new Contact().name("Platform Team").email("platform@example.com"))
                        .license(new License().name("Internal")))
                .servers(List.of(
                        new Server().url("http://localhost:8083").description("Local direct"),
                        new Server().url("http://localhost:8080").description("Via API Gateway"),
                        new Server().url("http://ticketing.local").description("K8s ingress")))
                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                .components(new Components()
                        .addSecuritySchemes(schemeName, new SecurityScheme()
                                .name(schemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste the `accessToken` returned by POST /api/auth/login")));
    }
}
