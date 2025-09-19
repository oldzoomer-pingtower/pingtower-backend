package ru.oldzoomer.pingtower.pinger.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import ru.oldzoomer.pingtower.pinger.dto.CheckConfiguration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SettingsConsumer {
    // Временное хранилище конфигураций проверок
    private final Map<String, CheckConfiguration> checkConfigurations = new ConcurrentHashMap<>();

    /**
     * Слушатель Kafka для получения обновлений настроек проверок от Settings Manager
     * @param settingsUpdate обновление настроек
     */
    @KafkaListener(topics = "pingtower.settings.updates", groupId = "pinger-settings-group")
    public void consumeSettingsUpdate(Map<String, Object> settingsUpdate,
                                      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                      @Header(KafkaHeaders.OFFSET) long offset) {
        if (settingsUpdate == null) {
            log.warn("Received null settings update from Kafka topic: {}, partition: {}, offset: {}",
                    topic, partition, offset);
            return;
        }

        try {
            log.info("Received settings update from Kafka - topic: {}, partition: {}, offset: {}",
                    topic, partition, offset);

            // Извлечение данных из сообщения
            String settingId = (String) settingsUpdate.get("settingId");
            String module = (String) settingsUpdate.get("module");
            String key = (String) settingsUpdate.get("key");
            Object value = settingsUpdate.get("value");
            String action = (String) settingsUpdate.get("action");

            log.info("Settings update details - ID: {}, Module: {}, Key: {}, Value: {}, Action: {}",
                    settingId, module, key, value, action);

            // Проверка обязательных полей
            if (module == null || module.isEmpty()) {
                log.warn("Settings update with null or empty module: {}", settingsUpdate);
                return;
            }

            // Если модуль настроек относится к pinger, обновляем конфигурацию
            if ("pinger".equals(module)) {
                if (key != null && !key.isEmpty()) {
                    log.info("Processing pinger configuration update: {} = {}", key, value);
                    
                    // Преобразуем значение в CheckConfiguration
                    if (value instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> configMap = (Map<String, Object>) value;
                        
                        CheckConfiguration config = new CheckConfiguration();
                        config.setId((String) configMap.get("id"));
                        config.setType((String) configMap.get("type"));
                        config.setResourceUrl((String) configMap.get("resourceUrl"));
                        config.setFrequency(((Number) configMap.get("frequency")).longValue());
                        config.setTimeout(((Number) configMap.get("timeout")).intValue());
                        config.setExpectedStatusCode((Integer) configMap.get("expectedStatusCode"));
                        config.setExpectedResponseTime(((Number) configMap.get("expectedResponseTime")).longValue());
                        config.setValidateSsl((Boolean) configMap.get("validateSsl"));
                        
                        // Обновляем хранилище конфигураций
                        if ("CREATE".equals(action) || "UPDATE".equals(action)) {
                            checkConfigurations.put(config.getId(), config);
                            log.info("Added/Updated check configuration: {}", config.getId());
                        } else if ("DELETE".equals(action)) {
                            checkConfigurations.remove(config.getId());
                            log.info("Removed check configuration: {}", config.getId());
                        }
                    }
                } else {
                    log.warn("Settings update for pinger module with null or empty key: {}", settingsUpdate);
                }
            } else {
                log.debug("Ignoring settings update for module: {}", module);
            }

            log.debug("Successfully processed settings update: settingId={}", settingId);
        } catch (Exception e) {
            log.error("Failed to process settings update from Kafka - topic: {}, partition: {}, offset: {}",
                    topic, partition, offset, e);
        }
    }
    
    /**
     * Получение всех конфигураций проверок
     * @return карта конфигураций проверок
     */
    @Cacheable("check-configurations")
    public Map<String, CheckConfiguration> getCheckConfigurations() {
        return checkConfigurations;
    }
    
    /**
     * Получение конфигурации проверки по ID
     * @param checkId ID проверки
     * @return конфигурация проверки
     */
    @Cacheable(value = "check-configurations", key = "#checkId")
    public CheckConfiguration getCheckConfiguration(String checkId) {
        return checkConfigurations.get(checkId);
    }
    
    /**
     * Обновление конфигурации проверки
     * @param config конфигурация проверки
     */
    @CacheEvict(value = "check-configurations", key = "#config.getId()")
    public void updateCheckConfiguration(CheckConfiguration config) {
        checkConfigurations.put(config.getId(), config);
    }
    
    /**
     * Удаление конфигурации проверки
     * @param checkId ID проверки
     */
    @CacheEvict(value = "check-configurations", key = "#checkId")
    public void removeCheckConfiguration(String checkId) {
        checkConfigurations.remove(checkId);
    }
}