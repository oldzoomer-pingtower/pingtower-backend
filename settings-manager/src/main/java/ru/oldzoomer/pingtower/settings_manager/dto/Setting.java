package ru.oldzoomer.pingtower.settings_manager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(description = "Настройка системы")
public class Setting {
    @Schema(description = "Уникальный идентификатор настройки", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;
    
    @NotBlank(message = "Модуль не может быть пустым")
    @Size(min = 1, max = 50, message = "Модуль должен быть от 1 до 50 символов")
    @Schema(description = "Модуль, к которому относится настройка", example = "pinger")
    private String module;
    
    @NotBlank(message = "Ключ настройки не может быть пустым")
    @Size(min = 1, max = 100, message = "Ключ настройки должен быть от 1 до 100 символов")
    @Schema(description = "Ключ настройки", example = "timeout")
    private String key;
    
    @NotBlank(message = "Значение настройки не может быть пустым")
    @Size(max = 1000, message = "Значение настройки не должно превышать 1000 символов")
    @Schema(description = "Значение настройки", example = "5000")
    private String value;
    
    @Size(max = 500, message = "Описание настройки не должно превышать 500 символов")
    @Schema(description = "Описание настройки", example = "Таймаут проверки в миллисекундах")
    private String description;
    
    @Schema(description = "Дата и время создания настройки", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "Дата и время последнего обновления настройки", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;
    
    @Schema(description = "Версия настройки", example = "1")
    private Integer version;
}