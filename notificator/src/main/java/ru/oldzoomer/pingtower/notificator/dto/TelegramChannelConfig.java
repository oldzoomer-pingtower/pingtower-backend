package ru.oldzoomer.pingtower.notificator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Конфигурация Telegram канала уведомлений")
public class TelegramChannelConfig {
    @Schema(description = "Токен Telegram бота", example = "1234567890:ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghi")
    private String botToken;
    
    @Schema(description = "ID чата для отправки уведомлений", example = "123456789")
    private String chatId;
}