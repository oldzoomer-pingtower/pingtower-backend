package ru.oldzoomer.pingtower.notificator.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.oldzoomer.pingtower.notificator.entity.ConfigurationSettingEntity;
import ru.oldzoomer.pingtower.notificator.mapper.ConfigurationSettingMapper;
import ru.oldzoomer.pingtower.notificator.repository.ConfigurationSettingRepository;

import java.util.Optional;

@Slf4j
@Service
public class ConfigurationService {
    
    private final ConfigurationSettingRepository configurationSettingRepository;
    private final ConfigurationSettingMapper configurationSettingMapper;
    
    public ConfigurationService(ConfigurationSettingRepository configurationSettingRepository, 
                               ConfigurationSettingMapper configurationSettingMapper) {
        this.configurationSettingRepository = configurationSettingRepository;
        this.configurationSettingMapper = configurationSettingMapper;
    }
    
    @PostConstruct
    public void initializeDefaultSettings() {
        log.info("Initializing ConfigurationService with default settings");
        
        // Check if settings already exist, if not, create defaults
        if (configurationSettingRepository.count() == 0) {
            log.info("No existing settings found, initializing with defaults");
            
            createOrUpdateSetting("email.smtp.server", "smtp.example.com", "STRING");
            createOrUpdateSetting("email.smtp.port", "587", "INTEGER");
            createOrUpdateSetting("telegram.bot.token", "your-bot-token", "STRING");
            createOrUpdateSetting("webhook.url", "https://example.com/webhook", "STRING");
            createOrUpdateSetting("grouping.interval", "3000", "LONG");
            createOrUpdateSetting("escalation.enabled", "true", "BOOLEAN");
        }
        
        log.info("ConfigurationService initialized with {} settings", configurationSettingRepository.count());
    }
    
    private void createOrUpdateSetting(String key, String value, String dataType) {
        Optional<ConfigurationSettingEntity> existingSetting = configurationSettingRepository.findByKey(key);
        ConfigurationSettingEntity setting;
        
        if (existingSetting.isPresent()) {
            setting = existingSetting.get();
            setting.setValue(value);
            setting.setDataType(dataType);
        } else {
            setting = new ConfigurationSettingEntity();
            setting.setKey(key);
            setting.setValue(value);
            setting.setDataType(dataType);
        }
        
        configurationSettingRepository.save(setting);
        log.info("Initialized setting '{}': {}", key, value);
    }
    
    @Cacheable(value = "settings", key = "#key")
    public Object getSetting(String key) {
        if (key == null || key.isEmpty()) {
            log.warn("Attempt to get setting with null or empty key");
            return null;
        }
        
        Optional<ConfigurationSettingEntity> settingEntity = configurationSettingRepository.findByKey(key);
        if (settingEntity.isPresent()) {
            ConfigurationSettingEntity entity = settingEntity.get();
            Object value = configurationSettingMapper.valueToObject(entity.getValue(), entity.getDataType());
            log.debug("Retrieved setting '{}': {}", key, value);
            return value;
        }
        
        log.debug("Setting '{}' not found", key);
        return null;
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
    @CacheEvict(value = {"settings", "stringSettings", "booleanSettings", "intSettings"}, key = "#key")
    public void updateSetting(String key, Object value) {
        if (key == null || key.isEmpty()) {
            log.warn("Attempt to update setting with null or empty key");
            return;
        }
        
        log.info("Updating setting '{}': {}", key, value);
        
        Optional<ConfigurationSettingEntity> existingSetting = configurationSettingRepository.findByKey(key);
        ConfigurationSettingEntity setting;
        
        if (existingSetting.isPresent()) {
            setting = existingSetting.get();
        } else {
            setting = new ConfigurationSettingEntity();
            setting.setKey(key);
        }
        
        setting.setValue(configurationSettingMapper.objectToValue(value));
        setting.setDataType(configurationSettingMapper.determineDataType(value));
        
        configurationSettingRepository.save(setting);
    }
}