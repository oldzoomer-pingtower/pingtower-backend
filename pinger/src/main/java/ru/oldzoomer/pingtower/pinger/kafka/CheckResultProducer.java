package ru.oldzoomer.pingtower.pinger.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.oldzoomer.pingtower.pinger.dto.CheckResult;

@Slf4j
@Component
@RequiredArgsConstructor
public class CheckResultProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    private static final String CHECK_RESULTS_TOPIC = "pingtower.check.results";
    
    /**
     * Отправка результата проверки в Kafka топик для Statistics модуля
     * @param checkResult результат проверки
     */
    public void sendCheckResult(CheckResult checkResult) {
        if (checkResult == null) {
            log.warn("Attempted to send null check result to Kafka");
            return;
        }
        
        try {
            kafkaTemplate.send(CHECK_RESULTS_TOPIC, checkResult.getCheckId(), checkResult);
            log.info("Sent check result to Kafka topic: {} for checkId: {}", CHECK_RESULTS_TOPIC, checkResult.getCheckId());
        } catch (Exception e) {
            log.error("Failed to send check result to Kafka topic: {} for checkId: {}", CHECK_RESULTS_TOPIC, checkResult.getCheckId(), e);
        }
    }
}