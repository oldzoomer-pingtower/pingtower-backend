package ru.oldzoomer.pingtower.statistics.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.oldzoomer.pingtower.statistics.cassandra.entity.RawCheckResult;
import ru.oldzoomer.pingtower.statistics.cassandra.repository.RawCheckResultRepository;
import ru.oldzoomer.pingtower.statistics.dto.CheckResult;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsProcessingService {
    private final RawCheckResultRepository rawCheckResultRepository;
    private final RedisCacheService redisCacheService;
    private final AggregationService aggregationService;
    
    /**
     * Обработка результата проверки
     * @param checkResult результат проверки
     */
    public void processCheckResult(CheckResult checkResult) {
        try {
            // Сохраняем сырые данные в Cassandra
            saveRawCheckResult(checkResult);
            
            // Обновляем кэш с последним результатом
            redisCacheService.cacheLatestResult(checkResult);
            
            // Передаем данные в сервис агрегации
            aggregationService.processCheckResultForAggregation(checkResult);
            
            log.debug("Successfully processed check result: checkId={}", checkResult.getCheckId());
        } catch (Exception e) {
            log.error("Failed to process check result: checkId={}", checkResult.getCheckId(), e);
        }
    }
    
    /**
     * Сохранение сырых данных результата проверки в Cassandra
     * @param checkResult результат проверки
     */
    private void saveRawCheckResult(CheckResult checkResult) {
        try {
            RawCheckResult rawCheckResult = new RawCheckResult();
            
            // Создаем ключ
            RawCheckResult.RawCheckResultKey key = new RawCheckResult.RawCheckResultKey();
            key.setCheckId(checkResult.getCheckId());
            key.setTimestamp(checkResult.getTimestamp());
            rawCheckResult.setKey(key);
            
            // Заполняем поля
            rawCheckResult.setStatus(checkResult.getStatus());
            rawCheckResult.setResponseTime(Math.toIntExact(checkResult.getResponseTime()));
            rawCheckResult.setHttpStatusCode(checkResult.getHttpStatusCode());
            rawCheckResult.setErrorMessage(checkResult.getErrorMessage());
            
            // Преобразуем метрики в Map
            if (checkResult.getMetrics() != null) {
                Map<String, String> metricsMap = new HashMap<>();
                metricsMap.put("connectionTime", String.valueOf(checkResult.getMetrics().getConnectionTime()));
                metricsMap.put("timeToFirstByte", String.valueOf(checkResult.getMetrics().getTimeToFirstByte()));
                if (checkResult.getMetrics().getSslValid() != null) {
                    metricsMap.put("sslValid", String.valueOf(checkResult.getMetrics().getSslValid()));
                }
                if (checkResult.getMetrics().getSslExpirationDate() != null) {
                    metricsMap.put("sslExpirationDate", checkResult.getMetrics().getSslExpirationDate().toString());
                }
                rawCheckResult.setMetrics(metricsMap);
            }
            
            // Сохраняем в Cassandra
            rawCheckResultRepository.save(rawCheckResult);
            log.debug("Saved raw check result to Cassandra: checkId={}", checkResult.getCheckId());
        } catch (Exception e) {
            log.error("Failed to save raw check result to Cassandra: checkId={}", checkResult.getCheckId(), e);
        }
    }
}