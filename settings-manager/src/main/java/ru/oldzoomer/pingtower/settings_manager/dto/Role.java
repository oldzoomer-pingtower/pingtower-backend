package ru.oldzoomer.pingtower.settings_manager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Schema(description = "Роль пользователя")
public class Role {
    @Schema(description = "Уникальный идентификатор роли", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;
    
    @NotBlank(message = "Название роли не может быть пустым")
    @Size(min = 1, max = 50, message = "Название роли должно быть от 1 до 50 символов")
    @Schema(description = "Название роли", example = "ADMIN")
    private String name;
    
    @Size(max = 255, message = "Описание роли не должно превышать 255 символов")
    @Schema(description = "Описание роли", example = "Администратор системы")
    private String description;
    
    @Schema(description = "Список разрешений роли", example = "[\"users.read\", \"users.write\"]")
    private List<String> permissions;
    
    @Schema(description = "Дата и время создания роли", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "Дата и время последнего обновления роли", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;
}