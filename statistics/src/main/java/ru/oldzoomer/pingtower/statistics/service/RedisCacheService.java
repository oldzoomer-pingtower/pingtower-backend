package ru.oldzoomer.pingtower.statistics.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.oldzoomer.pingtower.statistics.dto.CheckResult;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String LATEST_RESULT_KEY_PREFIX = "stats:check:";
    private static final String AGGREGATED_KEY_PREFIX = "stats:check:aggregated:";
    private static final Duration DEFAULT_CACHE_DURATION = Duration.ofHours(1);
    
    /**
     * Сохранение последнего результата проверки в кэш
     * @param checkResult результат проверки
     */
    public void cacheLatestResult(CheckResult checkResult) {
        try {
            String key = LATEST_RESULT_KEY_PREFIX + checkResult.getCheckId() + ":latest";
            redisTemplate.opsForValue().set(key, checkResult, DEFAULT_CACHE_DURATION);
            log.debug("Cached latest result for checkId: {}", checkResult.getCheckId());
        } catch (Exception e) {
            log.error("Failed to cache latest result for checkId: {}", checkResult.getCheckId(), e);
        }
    }
    
    /**
     * Получение последнего результата проверки из кэша
     * @param checkId идентификатор проверки
     * @return результат проверки или null, если не найден
     */
    public CheckResult getLatestResult(String checkId) {
        try {
            String key = LATEST_RESULT_KEY_PREFIX + checkId + ":latest";
            return (CheckResult) redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Failed to get cached latest result for checkId: {}", checkId, e);
            return null;
        }
    }
    
    /**
     * Сохранение агрегированных данных в кэш
     * @param checkId идентификатор проверки
     * @param interval интервал агрегации
     * @param data агрегированные данные
     */
    public void cacheAggregatedData(String checkId, String interval, Object data) {
        try {
            String key = AGGREGATED_KEY_PREFIX + checkId + ":" + interval;
            redisTemplate.opsForValue().set(key, data, DEFAULT_CACHE_DURATION);
            log.debug("Cached aggregated data for checkId: {}, interval: {}", checkId, interval);
        } catch (Exception e) {
            log.error("Failed to cache aggregated data for checkId: {}, interval: {}", checkId, interval, e);
        }
    }
    
    /**
     * Получение агрегированных данных из кэша
     * @param checkId идентификатор проверки
     * @param interval интервал агрегации
     * @return агрегированные данные или null, если не найдены
     */
    public Object getAggregatedData(String checkId, String interval) {
        try {
            String key = AGGREGATED_KEY_PREFIX + checkId + ":" + interval;
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Failed to get cached aggregated data for checkId: {}, interval: {}", checkId, interval, e);
            return null;
        }
    }
    
    /**
     * Удаление данных из кэша по идентификатору проверки
     * @param checkId идентификатор проверки
     */
    public void evictCache(String checkId) {
        try {
            String latestKey = LATEST_RESULT_KEY_PREFIX + checkId + ":latest";
            redisTemplate.delete(latestKey);
            
            // Удаление всех агрегированных данных для проверки
            String pattern = AGGREGATED_KEY_PREFIX + checkId + ":*";
            // В реальной реализации здесь нужно использовать SCAN для поиска ключей по шаблону
            // и удалить их все
            
            log.debug("Evicted cache for checkId: {}", checkId);
        } catch (Exception e) {
            log.error("Failed to evict cache for checkId: {}", checkId, e);
        }
    }
}