package ru.oldzoomer.pingtower.notificator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Map;

@Data
@Schema(description = "Конфигурация Webhook канала уведомлений")
public class WebhookChannelConfig {
    @Schema(description = "URL вебхука", example = "https://example.com/webhook")
    private String url;
    
    @Schema(description = "HTTP метод", example = "POST", allowableValues = {"GET", "POST", "PUT", "DELETE"})
    private String method;
    
    @Schema(description = "HTTP заголовки", example = "{\"Content-Type\": \"application/json\", \"Authorization\": \"Bearer token\"}")
    private Map<String, String> headers;
    
    @Schema(description = "Шаблон тела запроса", example = "{\"message\": \"{{message}}\", \"timestamp\": \"{{timestamp}}\"}")
    private String bodyTemplate;
}