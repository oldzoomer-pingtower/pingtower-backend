package ru.oldzoomer.pingtower.notificator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationChannel {
    private final ConfigurationService configurationService;
    
    @Override
    public void send(String message) {
        if (message == null || message.isEmpty()) {
            log.warn("Attempt to send empty Telegram notification");
            return;
        }
        
        try {
            log.info("Sending Telegram notification: {}", message);
            
            // В реальной реализации здесь будет логика отправки сообщения через Telegram API
            // с использованием настроек из ConfigurationService
            String botToken = configurationService.getStringSetting("telegram.bot.token");
            String chatId = configurationService.getStringSetting("telegram.chat.id");
            
            if (botToken == null || botToken.isEmpty()) {
                log.warn("Telegram bot token not configured");
                return;
            }
            
            if (chatId == null || chatId.isEmpty()) {
                log.warn("Telegram chat ID not configured");
                return;
            }
            
            // Пример отправки через Telegram (закомментирован для демонстрации)
            /*
            // Использование Telegram Bot API
            String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";
            // ... реализация HTTP запроса
            log.info("Telegram notification sent successfully to chat {}", chatId);
            */
        } catch (Exception e) {
            log.error("Failed to send Telegram notification: {}", e.getMessage(), e);
        }
    }

    @Override
    public String getChannelType() {
        return "TELEGRAM";
    }
}