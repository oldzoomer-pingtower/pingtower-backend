package ru.oldzoomer.pingtower.notificator.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI notificatorOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Notificator API")
                        .description("API для управления уведомлениями")
                        .version("v1.0")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")));
    }
}