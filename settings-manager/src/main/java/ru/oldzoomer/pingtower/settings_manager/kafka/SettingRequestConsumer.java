package ru.oldzoomer.pingtower.settings_manager.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.oldzoomer.pingtower.settings_manager.dto.SettingRequestMessage;

@Service
public class SettingRequestConsumer {

    private static final String SETTINGS_REQUESTS_TOPIC = "pingtower.settings.requests";

    @KafkaListener(topics = SETTINGS_REQUESTS_TOPIC)
    public void listenSettingRequests(SettingRequestMessage message) {
        // Обработка запроса на изменение настроек
        // В реальной реализации здесь будет логика обработки запроса
        System.out.println("Received setting request: " + message);
        
        // TODO: Реализовать обработку запроса на изменение настроек
        // Это может включать валидацию, применение изменений и отправку подтверждения
    }
}