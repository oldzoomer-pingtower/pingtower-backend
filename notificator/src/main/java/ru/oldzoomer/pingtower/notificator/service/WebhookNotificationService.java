package ru.oldzoomer.pingtower.notificator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookNotificationService implements NotificationChannel {
    private final ConfigurationService configurationService;
    
    @Override
    public void send(String message) {
        if (message == null || message.isEmpty()) {
            log.warn("Attempt to send empty Webhook notification");
            return;
        }
        
        try {
            log.info("Sending Webhook notification: {}", message);
            
            // В реальной реализации здесь будет логика отправки webhook
            // с использованием настроек из ConfigurationService
            String webhookUrl = configurationService.getStringSetting("webhook.url");
            
            if (webhookUrl == null || webhookUrl.isEmpty()) {
                log.warn("Webhook URL not configured");
            }
            
            // Пример отправки webhook (закомментирован для демонстрации)
            /*
            // Использование HTTP клиента для отправки webhook
            // Отправка HTTP POST запроса с данными уведомления
            // ... реализация HTTP запроса
            log.info("Webhook notification sent successfully to {}", webhookUrl);
            */
        } catch (Exception e) {
            log.error("Failed to send Webhook notification: {}", e.getMessage(), e);
        }
    }

    @Override
    public String getChannelType() {
        return "WEBHOOK";
    }
}