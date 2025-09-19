package ru.oldzoomer.pingtower.notificator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ConfigurationService {
    // В реальной реализации здесь будет логика получения настроек
    // из внешних источников (Settings Manager через Kafka или локальные конфигурации)
    
    private final Map<String, Object> configuration = new HashMap<>();
    
    public ConfigurationService() {
        // Инициализация с дефолтными значениями для демонстрации
        log.info("Initializing ConfigurationService with default settings");
        configuration.put("email.smtp.server", "smtp.example.com");
        configuration.put("email.smtp.port", 587);
        configuration.put("telegram.bot.token", "your-bot-token");
        configuration.put("webhook.url", "https://example.com/webhook");
        configuration.put("grouping.interval", 3000L); // 5 минут
        configuration.put("escalation.enabled", true);
        log.info("ConfigurationService initialized with {} settings", configuration.size());
    }
    
    @Cacheable(value = "settings", key = "#key")
    public Object getSetting(String key) {
        if (key == null || key.isEmpty()) {
            log.warn("Attempt to get setting with null or empty key");
            return null;
        }
        
        Object value = configuration.get(key);
        log.debug("Retrieved setting '{}': {}", key, value);
        return value;
    }
    
    @Cacheable(value = "stringSettings", key = "#key")
    public String getStringSetting(String key) {
        Object value = getSetting(key);
        return value != null ? value.toString() : null;
    }
    
    @Cacheable(value = "intSettings", key = "#key")
    public Integer getIntSetting(String key) {
        Object value = getSetting(key);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value != null) {
            try {
                return Integer.parseInt(value.toString());
            } catch (NumberFormatException e) {
                log.warn("Failed to parse integer setting '{}': {}", key, value, e);
                return null;
            }
        }
        return null;
    }
    
    @Cacheable(value = "booleanSettings", key = "#key")
    public Boolean getBooleanSetting(String key) {
        Object value = getSetting(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value != null) {
            return Boolean.parseBoolean(value.toString());
        }
        return null;
    }
    
    public Long getLongSetting(String key) {
        Object value = getSetting(key);
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value != null) {
            try {
                return Long.parseLong(value.toString());
            } catch (NumberFormatException e) {
                log.warn("Failed to parse long setting '{}': {}", key, value, e);
                return null;
            }
        }
        return null;
    }
    
    // Методы для получения специфичных настроек
    public String getEmailSmtpServer() {
        return getStringSetting("email.smtp.server");
    }
    
    public Integer getEmailSmtpPort() {
        return getIntSetting("email.smtp.port");
    }
    
    public String getTelegramBotToken() {
        return getStringSetting("telegram.bot.token");
    }
    
    public String getWebhookUrl() {
        return getStringSetting("webhook.url");
    }
    
    public Long getGroupingInterval() {
        return getLongSetting("grouping.interval");
    }
    
    public Boolean isEscalationEnabled() {
        return getBooleanSetting("escalation.enabled");
    }
    
    // Метод для обновления настроек
    @CacheEvict(value = {"settings", "stringSettings", "booleanSettings", "intSettings", "longSettings"}, key = "#key")
    public void updateSetting(String key, Object value) {
        if (key == null || key.isEmpty()) {
            log.warn("Attempt to update setting with null or empty key");
            return;
        }
        
        log.info("Updating setting '{}': {}", key, value);
        configuration.put(key, value);
    }
}