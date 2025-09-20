package ru.oldzoomer.pingtower.settings_manager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(description = "Сообщение запроса изменения настройки")
public class SettingRequestMessage {
    @Schema(description = "Уникальный идентификатор запроса", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID requestId;
    
    @Schema(description = "Идентификатор модуля", example = "pinger")
    private String moduleId;
    
    @Schema(description = "Ключ настройки", example = "timeout")
    private String settingKey;
    
    @Schema(description = "Новое значение настройки", example = "5000")
    private String newValue;
    
    @Schema(description = "Идентификатор пользователя, запросившего изменение", example = "user123")
    private String requestedBy;
    
    @Schema(description = "Временная метка запроса", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;
    
    @Schema(description = "Причина изменения настройки", example = "Увеличение таймаута для медленных сетей")
    private String reason;
}