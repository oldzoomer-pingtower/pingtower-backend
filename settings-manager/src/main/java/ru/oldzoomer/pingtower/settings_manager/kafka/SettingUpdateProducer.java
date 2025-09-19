package ru.oldzoomer.pingtower.settings_manager.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.oldzoomer.pingtower.settings_manager.dto.SettingUpdateMessage;

@Service
public class SettingUpdateProducer {

    private static final String SETTINGS_UPDATES_TOPIC = "pingtower.settings.updates";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public SettingUpdateProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendSettingUpdate(SettingUpdateMessage message) {
        kafkaTemplate.send(SETTINGS_UPDATES_TOPIC, message.getSettingId().toString(), message);
    }
}