package ru.oldzoomer.pingtower.pinger.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "Сообщение об оповещении о состоянии проверки")
public class AlertMessage {
    @Schema(description = "Идентификатор проверки", example = "check-12345")
    private String checkId;
    
    @Schema(description = "URL ресурса", example = "https://example.com")
    private String resourceUrl;
    
    @Schema(description = "Время возникновения оповещения", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;
    
    @Schema(description = "Текущий статус проверки", example = "FAILURE", allowableValues = {"SUCCESS", "FAILURE", "TIMEOUT", "ERROR"})
    private String status;
    
    @Schema(description = "Продолжительность простоя в миллисекундах", example = "300000")
    private long downtimeDuration;
    
    @Schema(description = "Сообщение об ошибке", example = "Connection timeout")
    private String errorMessage;
    
    @Schema(description = "Предыдущий статус проверки", example = "SUCCESS", allowableValues = {"SUCCESS", "FAILURE", "TIMEOUT", "ERROR"})
    private String previousStatus;
}