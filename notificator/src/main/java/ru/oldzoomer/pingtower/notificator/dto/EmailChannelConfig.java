package ru.oldzoomer.pingtower.notificator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "Конфигурация email канала уведомлений")
public class EmailChannelConfig {
    @Schema(description = "SMTP сервер", example = "smtp.gmail.com")
    private String smtpServer;
    
    @Schema(description = "SMTP порт", example = "587")
    private Integer smtpPort;
    
    @Schema(description = "Имя пользователя", example = "user@example.com")
    private String username;
    
    @Schema(description = "Пароль", example = "your_password")
    private String password;
    
    @Schema(description = "Email отправителя", example = "noreply@example.com")
    private String fromAddress;
    
    @Schema(description = "Список email получателей", example = "[\"admin@example.com\", \"support@example.com\"]")
    private List<String> toAddresses;
}