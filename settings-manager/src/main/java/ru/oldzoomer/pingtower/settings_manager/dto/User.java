package ru.oldzoomer.pingtower.settings_manager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(description = "Пользователь системы")
public class User {
    @Schema(description = "Уникальный идентификатор пользователя", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;
    
    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
    @Schema(description = "Имя пользователя (логин)", example = "john_doe")
    private String username;
    
    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Неверный формат электронной почты")
    @Size(max = 100, message = "Электронная почта не должна превышать 100 символов")
    @Schema(description = "Электронная почта пользователя", example = "john.doe@example.com")
    private String email;
    
    @Size(max = 50, message = "Имя пользователя не должно превышать 50 символов")
    @Schema(description = "Имя пользователя", example = "John")
    private String firstName;
    
    @Size(max = 50, message = "Фамилия пользователя не должна превышать 50 символов")
    @Schema(description = "Фамилия пользователя", example = "Doe")
    private String lastName;
    
    @Schema(description = "Дата и время создания пользователя", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "Дата и время последнего обновления пользователя", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;
}