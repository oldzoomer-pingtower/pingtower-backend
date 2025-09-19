package ru.oldzoomer.pingtower.statistics.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.oldzoomer.pingtower.statistics.dto.CheckResult;
import ru.oldzoomer.pingtower.statistics.service.StatisticsProcessingService;

@Slf4j
@Component
@RequiredArgsConstructor
public class CheckResultConsumer {
    private final StatisticsProcessingService statisticsProcessingService;
    
    private static final String CHECK_RESULTS_TOPIC = "pingtower.check.results";
    private static final String CONSUMER_GROUP_ID = "statistics-check-results-group";
    
    /**
     * Слушатель Kafka для получения результатов проверок от Pinger
     * @param checkResult результат проверки
     */
    @KafkaListener(topics = CHECK_RESULTS_TOPIC, groupId = CONSUMER_GROUP_ID)
    public void consumeCheckResult(CheckResult checkResult,
                                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                   @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                   @Header(KafkaHeaders.OFFSET) long offset) {
        if (checkResult == null) {
            log.warn("Received null check result from Kafka topic: {}, partition: {}, offset: {}",
                    topic, partition, offset);
            return;
        }
        
        try {
            log.info("Received check result from Kafka - topic: {}, partition: {}, offset: {}, checkId: {}, resourceUrl: {}, status: {}",
                    topic, partition, offset,
                    checkResult.getCheckId(), checkResult.getResourceUrl(), checkResult.getStatus());
            
            // Проверка обязательных полей
            if (checkResult.getCheckId() == null || checkResult.getCheckId().isEmpty()) {
                log.warn("Check result with null or empty checkId: {}", checkResult);
                return;
            }
            
            // Передаем результат в StatisticsProcessingService для обработки
            statisticsProcessingService.processCheckResult(checkResult);
            
            log.debug("Successfully processed check result: checkId={}", checkResult.getCheckId());
        } catch (Exception e) {
            log.error("Failed to process check result: checkId={}, resourceUrl={}, topic={}, partition={}, offset={}",
                    checkResult.getCheckId(), checkResult.getResourceUrl(),
                    topic, partition, offset, e);
        }
    }
}