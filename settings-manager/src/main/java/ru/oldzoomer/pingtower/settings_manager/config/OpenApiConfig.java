package ru.oldzoomer.pingtower.settings_manager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI settingsManagerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Settings Manager API")
                        .description("API для управления настройками системы мониторинга PingTower")
                        .version("v1.0")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")));
    }
}