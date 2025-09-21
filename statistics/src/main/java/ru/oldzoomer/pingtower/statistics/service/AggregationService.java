package ru.oldzoomer.pingtower.statistics.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.oldzoomer.pingtower.statistics.cassandra.entity.AggregatedCheckResult;
import ru.oldzoomer.pingtower.statistics.cassandra.repository.AggregatedCheckResultRepository;
import ru.oldzoomer.pingtower.statistics.dto.CheckResult;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregationService {
    private final AggregatedCheckResultRepository aggregatedCheckResultRepository;
    
    // Хранилище для промежуточных агрегированных данных
    private final Map<String, AggregationData> aggregationDataMap = new ConcurrentHashMap<>();
    
    /**
     * Обработка результата проверки для агрегации
     * @param checkResult результат проверки
     */
    public void processCheckResultForAggregation(CheckResult checkResult) {
        try {
            // Используем агрегацию по часам по умолчанию
            String interval = "HOUR";
            aggregateCheckResult(checkResult, interval);
            
            log.debug("Processed check result for aggregation: checkId={}", checkResult.getCheckId());
        } catch (Exception e) {
            log.error("Failed to process check result for aggregation: checkId={}", checkResult.getCheckId(), e);
        }
    }
    
    /**
     * Агрегация результата проверки по заданному интервалу
     * @param checkResult результат проверки
     * @param interval интервал агрегации (MINUTE, HOUR, DAY, WEEK, MONTH)
     */
    private void aggregateCheckResult(CheckResult checkResult, String interval) {
        try {
            String key = checkResult.getCheckId() + ":" + interval;
            AggregationData aggregationData = aggregationDataMap.computeIfAbsent(key, k -> new AggregationData());
            
            // Обновляем промежуточные данные
            aggregationData.update(checkResult);
            
            // Проверяем, нужно ли сохранить агрегированные данные
            if (shouldSaveAggregatedData(checkResult.getTimestamp(), interval, aggregationData)) {
                saveAggregatedData(checkResult.getCheckId(), interval, aggregationData);
                // Сбрасываем данные после сохранения
                aggregationData.reset();
            }
            
            log.debug("Aggregated check result: checkId={}, interval={}", checkResult.getCheckId(), interval);
        } catch (Exception e) {
            log.error("Failed to aggregate check result: checkId={}, interval={}", checkResult.getCheckId(), interval, e);
        }
    }
    
    /**
     * Проверка, нужно ли сохранить агрегированные данные
     * @param timestamp временная метка результата проверки
     * @param interval интервал агрегации
     * @param aggregationData промежуточные данные агрегации
     * @return true, если нужно сохранить данные
     */
    private boolean shouldSaveAggregatedData(LocalDateTime timestamp, String interval, AggregationData aggregationData) {
        // В реальной реализации здесь должна быть логика определения,
        // когда сохранять агрегированные данные (например, по истечении интервала)
        // Для упрощения примера возвращаем true, если накоплено достаточно данных
        return aggregationData.getCount() >= 10; // Пример: сохраняем каждые 10 записей
    }
    
    /**
     * Сохранение агрегированных данных
     * @param checkId идентификатор проверки
     * @param interval интервал агрегации
     * @param aggregationData промежуточные данные агрегации
     */
    private void saveAggregatedData(String checkId, String interval, AggregationData aggregationData) {
        try {
            AggregatedCheckResult aggregatedCheckResult = new AggregatedCheckResult();
            
            // Создаем ключ
            AggregatedCheckResult.AggregatedCheckResultKey key = new AggregatedCheckResult.AggregatedCheckResultKey();
            key.setCheckId(checkId);
            key.setAggregationInterval(interval);
            key.setTimestamp(LocalDateTime.now()); // В реальной реализации нужно вычислить начало интервала
            aggregatedCheckResult.setKey(key);
            
            // Заполняем поля агрегированными данными
            aggregatedCheckResult.setUpCount(aggregationData.getUpCount());
            aggregatedCheckResult.setDownCount(aggregationData.getDownCount());
            aggregatedCheckResult.setUnknownCount(aggregationData.getUnknownCount());
            aggregatedCheckResult.setAvgResponseTime(aggregationData.getAvgResponseTime());
            aggregatedCheckResult.setMinResponseTime(aggregationData.getMinResponseTime());
            aggregatedCheckResult.setMaxResponseTime(aggregationData.getMaxResponseTime());
            
            // Сохраняем в Cassandra
            aggregatedCheckResultRepository.save(aggregatedCheckResult);
            
            log.debug("Saved aggregated data: checkId={}, interval={}", checkId, interval);
        } catch (Exception e) {
            log.error("Failed to save aggregated data: checkId={}, interval={}", checkId, interval, e);
        }
    }
    
    /**
     * Класс для хранения промежуточных данных агрегации
     */
    private static class AggregationData {
        @Getter
        private int count = 0;
        @Getter
        private int upCount = 0;
        @Getter
        private int downCount = 0;
        @Getter
        private int unknownCount = 0;
        private long totalResponseTime = 0;
        private int minResponseTime = Integer.MAX_VALUE;
        private int maxResponseTime = Integer.MIN_VALUE;
        
        public synchronized void update(CheckResult checkResult) {
            count++;
            
            // Обновляем счетчики статусов
            switch (checkResult.getStatus()) {
                case "UP":
                    upCount++;
                    break;
                case "DOWN":
                    downCount++;
                    break;
                default:
                    unknownCount++;
                    break;
            }
            
            // Обновляем данные о времени отклика
            int responseTime = Math.toIntExact(checkResult.getResponseTime());
            totalResponseTime += responseTime;
            minResponseTime = Math.min(minResponseTime, responseTime);
            maxResponseTime = Math.max(maxResponseTime, responseTime);
        }
        
        public synchronized void reset() {
            count = 0;
            upCount = 0;
            downCount = 0;
            unknownCount = 0;
            totalResponseTime = 0;
            minResponseTime = Integer.MAX_VALUE;
            maxResponseTime = Integer.MIN_VALUE;
        }

        public Double getAvgResponseTime() {
            return count > 0 ? (double) totalResponseTime / count : 0.0;
        }
        
        public int getMinResponseTime() {
            return minResponseTime == Integer.MAX_VALUE ? 0 : minResponseTime;
        }
        
        public int getMaxResponseTime() {
            return maxResponseTime == Integer.MIN_VALUE ? 0 : maxResponseTime;
        }
    }
}