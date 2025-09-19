package ru.oldzoomer.pingtower.pinger.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.oldzoomer.pingtower.pinger.dto.AlertMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    private static final String CHECK_ALERTS_TOPIC = "pingtower.check.alerts";
    
    /**
     * Отправка уведомления о недоступности ресурса в Kafka топик для Notificator модуля
     * @param alertMessage уведомление о недоступности
     */
    public void sendAlert(AlertMessage alertMessage) {
        try {
            kafkaTemplate.send(CHECK_ALERTS_TOPIC, alertMessage.getCheckId(), alertMessage);
            log.info("Sent alert message to Kafka topic: {} for checkId: {}", CHECK_ALERTS_TOPIC, alertMessage.getCheckId());
        } catch (Exception e) {
            log.error("Failed to send alert message to Kafka topic: {} for checkId: {}", CHECK_ALERTS_TOPIC, alertMessage.getCheckId(), e);
        }
    }
}