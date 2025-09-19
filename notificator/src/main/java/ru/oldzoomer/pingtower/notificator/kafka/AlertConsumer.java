package ru.oldzoomer.pingtower.notificator.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import ru.oldzoomer.pingtower.notificator.dto.AlertMessage;
import ru.oldzoomer.pingtower.notificator.service.NotificationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertConsumer {
    private final NotificationService notificationService;
    
    /**
     * Слушатель Kafka для получения уведомлений от Pinger
     * @param alertMessage уведомление о недоступности ресурса
     */
    @KafkaListener(topics = "pingtower.check.alerts", groupId = "notificator-alerts-group")
    public void consumeAlert(AlertMessage alertMessage,
                             @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                             @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                             @Header(KafkaHeaders.OFFSET) long offset) {
        if (alertMessage == null) {
            log.warn("Received null alert message from Kafka topic: {}, partition: {}, offset: {}",
                    topic, partition, offset);
            return;
        }
        
        try {
            log.info("Received alert message from Kafka - topic: {}, partition: {}, offset: {}, checkId: {}, resourceUrl: {}, status: {}",
                    topic, partition, offset,
                    alertMessage.getCheckId(), alertMessage.getResourceUrl(), alertMessage.getStatus());
            
            // Проверка обязательных полей
            if (alertMessage.getCheckId() == null || alertMessage.getCheckId().isEmpty()) {
                log.warn("Alert message with null or empty checkId: {}", alertMessage);
                return;
            }
            
            // Передаем уведомление в NotificationService для обработки
            notificationService.processAlert(alertMessage);
            
            log.debug("Successfully processed alert message: checkId={}", alertMessage.getCheckId());
        } catch (Exception e) {
            log.error("Failed to process alert message: checkId={}, resourceUrl={}, topic={}, partition={}, offset={}",
                    alertMessage.getCheckId(), alertMessage.getResourceUrl(),
                    topic, partition, offset, e);
        }
    }
}