package ru.oldzoomer.pingtower.notificator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Map;

@Data
@Schema(description = "Канал уведомлений")
public class NotificationChannelDTO {
    @Schema(description = "Уникальный идентификатор канала", example = "channel-12345")
    private String id;
    
    @NotBlank(message = "Тип канала обязателен")
    @Schema(description = "Тип канала", example = "EMAIL", allowableValues = {"EMAIL", "TELEGRAM", "WEBHOOK"})
    private String type;
    
    @NotBlank(message = "Название канала обязательно")
    @Schema(description = "Название канала", example = "Email администратора")
    private String name;
    
    @NotNull(message = "Конфигурация канала обязательна")
    @Schema(description = "Конфигурация канала (зависит от типа)")
    private Map<String, Object> configuration;
    
    @Schema(description = "Включен ли канал", example = "true")
    private boolean enabled;
    
    @Schema(description = "Дата создания", example = "2024-01-15T10:30:00")
    private String createdAt;
    
    @Schema(description = "Дата последнего обновления", example = "2024-01-15T10:30:00")
    private String updatedAt;
}