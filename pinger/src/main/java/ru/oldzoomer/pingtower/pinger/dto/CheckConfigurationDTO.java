package ru.oldzoomer.pingtower.pinger.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Конфигурация проверки доступности ресурса")
public class CheckConfigurationDTO {
    @Schema(description = "Уникальный идентификатор проверки", example = "check-12345")
    private String id;
    
    @Schema(description = "Тип проверки", example = "HTTP", allowableValues = {"HTTP", "HTTPS", "TCP", "PING"})
    private String type;
    
    @Schema(description = "URL ресурса для проверки", example = "https://example.com")
    private String resourceUrl;
    
    @Schema(description = "Частота проверки в миллисекундах", example = "60000")
    private Long frequency;
    
    @Schema(description = "Таймаут проверки в миллисекундах", example = "5000")
    private Integer timeout;
    
    @Schema(description = "Ожидаемый HTTP статус код", example = "200")
    private Integer expectedStatusCode;
    
    @Schema(description = "Ожидаемое время ответа в миллисекундах", example = "1000")
    private Long expectedResponseTime;
    
    @Schema(description = "Проверять SSL сертификат", example = "true")
    private Boolean validateSsl;
    
    @Schema(description = "Дата и время создания проверки", example = "2024-01-15T10:30:00")
    private String createdAt;
    
    @Schema(description = "Дата и время последнего обновления проверки", example = "2024-01-15T10:30:00")
    private String updatedAt;
}