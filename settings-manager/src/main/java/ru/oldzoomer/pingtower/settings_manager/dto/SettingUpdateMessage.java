package ru.oldzoomer.pingtower.settings_manager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(description = "Сообщение об обновлении настройки")
public class SettingUpdateMessage {
    @Schema(description = "Уникальный идентификатор настройки", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID settingId;
    
    @Schema(description = "Модуль, к которому относится настройка", example = "pinger")
    private String module;
    
    @Schema(description = "Ключ настройки", example = "timeout")
    private String key;
    
    @Schema(description = "Значение настройки", example = "5000")
    private String value;
    
    @Schema(description = "Временная метка обновления", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;
    
    @Schema(description = "Версия настройки", example = "1")
    private Integer version;
    
    @Schema(description = "Действие с настройкой", example = "UPDATE", allowableValues = {"CREATE", "UPDATE", "DELETE"})
    private String action;
}