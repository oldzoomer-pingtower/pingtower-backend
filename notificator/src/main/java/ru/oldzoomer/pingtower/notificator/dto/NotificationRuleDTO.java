package ru.oldzoomer.pingtower.notificator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "Правило уведомлений")
public class NotificationRuleDTO {
    @Schema(description = "Уникальный идентификатор правила", example = "rule-12345")
    private String id;
    
    @NotBlank(message = "Название правила обязательно")
    @Schema(description = "Название правила", example = "Уведомление о недоступности")
    private String name;
    
    @NotNull(message = "Список условий обязателен")
    @Schema(description = "Список условий срабатывания правила")
    private List<Condition> conditions;
    
    @NotNull(message = "Список действий обязателен")
    @Schema(description = "Список действий при срабатывании правила")
    private List<Action> actions;
    
    @Schema(description = "Включено ли правило", example = "true")
    private boolean enabled;
    
    @Schema(description = "Дата создания", example = "2024-01-15T10:30:00")
    private String createdAt;
    
    @Schema(description = "Дата последнего обновления", example = "2024-01-15T10:30:00")
    private String updatedAt;
    
    @Data
    @Schema(description = "Условие срабатывания правила")
    public static class Condition {
        @Schema(description = "Поле для проверки", example = "status", allowableValues = {"status", "responseTime", "errorMessage"})
        private String field;
        
        @Schema(description = "Оператор сравнения", example = "EQUALS", allowableValues = {"EQUALS", "NOT_EQUALS", "GREATER_THAN", "LESS_THAN", "CONTAINS"})
        private String operator;
        
        @Schema(description = "Значение для сравнения", example = "FAILURE")
        private String value;
    }
    
    @Data
    @Schema(description = "Действие при срабатывании правила")
    public static class Action {
        @Schema(description = "Тип действия", example = "SEND_NOTIFICATION", allowableValues = {"SEND_NOTIFICATION"})
        private String type;
        
        @Schema(description = "Идентификатор канала для отправки уведомления", example = "channel-12345")
        private String channelId;
    }
}