package ru.oldzoomer.pingtower.statistics.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI statisticsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Statistics API")
                        .description("API для сбора, хранения и предоставления статистики мониторинга")
                        .version("v1.0")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")));
    }
}