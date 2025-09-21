package ru.oldzoomer.pingtower.statistics.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.oldzoomer.pingtower.statistics.cassandra.entity.RawCheckResult;
import ru.oldzoomer.pingtower.statistics.cassandra.repository.AggregatedCheckResultRepository;
import ru.oldzoomer.pingtower.statistics.cassandra.repository.RawCheckResultRepository;
import ru.oldzoomer.pingtower.statistics.dto.CheckResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsRetrievalService {
    private final RawCheckResultRepository rawCheckResultRepository;
    private final AggregatedCheckResultRepository aggregatedCheckResultRepository;
    
    /**
     * Получение последних результатов проверки
     * @param checkId идентификатор проверки
     * @return последние результаты проверки
     */
    public CheckResult getLatestCheckResult(String checkId) {
        try {
            List<RawCheckResult> rawResults = rawCheckResultRepository.findByKeyCheckIdOrderByKeyTimestampDesc(checkId);
            if (!rawResults.isEmpty()) {
                return convertToCheckResult(rawResults.getFirst());
            }
            return null;
        } catch (Exception e) {
            log.error("Failed to get latest check result for checkId: {}", checkId, e);
            return null;
        }
    }
    
    /**
     * Получение истории результатов проверки
     * @param checkId идентификатор проверки
     * @param from начальная дата
     * @param to конечная дата
     * @param limit ограничение на количество записей
     * @param offset смещение
     * @return история результатов проверки
     */
    public List<CheckResult> getCheckHistory(String checkId, LocalDateTime from, LocalDateTime to, int limit, int offset) {
        try {
            // Если даты не заданы, используем последние 24 часа
            if (from == null) {
                from = LocalDateTime.now().minusDays(1);
            }
            if (to == null) {
                to = LocalDateTime.now();
            }
            
            List<RawCheckResult> rawResults = rawCheckResultRepository.findByKeyCheckIdAndKeyTimestampBetween(checkId, from, to);
            
            // Применяем пагинацию
            int endIndex = Math.min(offset + limit, rawResults.size());
            int startIndex = Math.min(offset, rawResults.size());
            
            if (startIndex < endIndex) {
                return rawResults.subList(startIndex, endIndex).stream()
                        .map(this::convertToCheckResult)
                        .collect(Collectors.toList());
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            log.error("Failed to get check history for checkId: {}", checkId, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Получение агрегированных данных по проверке
     * @param checkId идентификатор проверки
     * @param interval интервал агрегации
     * @param from начальная дата
     * @param to конечная дата
     * @return агрегированные данные
     */
    public Object getAggregatedData(String checkId, String interval, LocalDateTime from, LocalDateTime to) {
        try {
            // Если даты не заданы, используем последние 7 дней
            if (from == null) {
                from = LocalDateTime.now().minusDays(7);
            }
            if (to == null) {
                to = LocalDateTime.now();
            }

            return aggregatedCheckResultRepository.findByKeyCheckIdAndKeyAggregationIntervalAndKeyTimestampBetween(
                checkId, interval, from, to);
        } catch (Exception e) {
            log.error("Failed to get aggregated data for checkId: {}, interval: {}", checkId, interval, e);
            return null;
        }
    }
    
    /**
     * Получение данных для дашборда
     * @return данные для дашборда
     */
    public Object getDashboardData() {
        try {
            // В реальной реализации здесь будет логика получения данных для дашборда
            // Например, общее количество проверок, количество доступных/недоступных ресурсов и т.д.
            
            // Для примера возвращаем простой объект
            return new DashboardData();
        } catch (Exception e) {
            log.error("Failed to get dashboard data", e);
            return null;
        }
    }
    
    /**
     * Преобразование RawCheckResult в CheckResult
     * @param rawCheckResult сырые данные результата проверки
     * @return CheckResult
     */
    private CheckResult convertToCheckResult(RawCheckResult rawCheckResult) {
        CheckResult checkResult = new CheckResult();
        checkResult.setCheckId(rawCheckResult.getKey().getCheckId());
        checkResult.setTimestamp(rawCheckResult.getKey().getTimestamp());
        checkResult.setStatus(rawCheckResult.getStatus());
        checkResult.setResponseTime(rawCheckResult.getResponseTime());
        checkResult.setHttpStatusCode(rawCheckResult.getHttpStatusCode());
        checkResult.setErrorMessage(rawCheckResult.getErrorMessage());
        
        // Преобразуем метрики
        if (rawCheckResult.getMetrics() != null) {
            CheckResult.Metrics metrics = new CheckResult.Metrics();
            metrics.setConnectionTime(Long.parseLong(rawCheckResult.getMetrics().getOrDefault("connectionTime", "0")));
            metrics.setTimeToFirstByte(Long.parseLong(rawCheckResult.getMetrics().getOrDefault("timeToFirstByte", "0")));
            
            String sslValidStr = rawCheckResult.getMetrics().get("sslValid");
            if (sslValidStr != null) {
                metrics.setSslValid(Boolean.parseBoolean(sslValidStr));
            }
            
            String sslExpirationDateStr = rawCheckResult.getMetrics().get("sslExpirationDate");
            if (sslExpirationDateStr != null) {
                // В реальной реализации здесь нужно правильно парсить дату
                // metrics.setSslExpirationDate(LocalDateTime.parse(sslExpirationDateStr));
            }
            
            checkResult.setMetrics(metrics);
        }
        
        return checkResult;
    }
    
    /**
     * Класс для данных дашборда
     */
    @Setter
    @Getter
    public static class DashboardData {
        // Геттеры и сеттеры
        private int totalChecks = 0;
        private int upChecks = 0;
        private int downChecks = 0;
        private int unknownChecks = 0;
        private double overallUptime = 0.0;

    }
}