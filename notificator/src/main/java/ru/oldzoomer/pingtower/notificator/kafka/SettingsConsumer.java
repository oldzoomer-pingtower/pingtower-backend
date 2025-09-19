package ru.oldzoomer.pingtower.notificator.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import ru.oldzoomer.pingtower.notificator.service.ConfigurationService;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SettingsConsumer {
    private final ConfigurationService configurationService;
    
    /**
     * Слушатель Kafka для получения обновлений настроек от Settings Manager
     * @param settingsUpdate обновление настроек
     */
    @KafkaListener(topics = "pingtower.settings.updates", groupId = "notificator-settings-group")
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
            
            // Если модуль настроек относится к notificator, обновляем конфигурацию
            if ("notificator".equals(module)) {
                if (key != null && !key.isEmpty()) {
                    log.info("Updating notificator configuration: {} = {}", key, value);
                    configurationService.updateSetting(key, value);
                } else {
                    log.warn("Settings update for notificator module with null or empty key: {}", settingsUpdate);
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
}