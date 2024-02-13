package org.example.messagingapp.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class ApplicationConfiguration {
    @Bean
    public TimestampProvider timestampProvider() {
        return LocalDateTime::now;
    }

    @Bean
    public OpenAPI openApiSpec() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Messaging Application API Documentation")
                        .description("Messaging REST API implemented with Spring Boot and documented using OpenAPI v3")
                );
    }
}
