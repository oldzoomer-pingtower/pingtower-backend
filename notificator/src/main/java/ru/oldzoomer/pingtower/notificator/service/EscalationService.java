package ru.oldzoomer.pingtower.notificator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import ru.oldzoomer.pingtower.notificator.dto.AlertMessage;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class EscalationService {
    private final ConfigurationService configurationService;
    
    // Хранилище состояния эскалации для каждого уведомления
    private final Map<String, EscalationState> escalationStates = new ConcurrentHashMap<>();
    
    public static class EscalationState {
        private int escalationLevel = 0;
        private LocalDateTime firstAlertTime = LocalDateTime.now();
        private LocalDateTime lastEscalationTime = LocalDateTime.now();
        
        public int getEscalationLevel() {
            return escalationLevel;
        }
        
        public void incrementEscalationLevel() {
            this.escalationLevel++;
            this.lastEscalationTime = LocalDateTime.now();
        }
        
        public LocalDateTime getFirstAlertTime() {
            return firstAlertTime;
        }
        
        public LocalDateTime getLastEscalationTime() {
            return lastEscalationTime;
        }
    }
    
    /**
     * Проверяет, нужно ли эскалировать уведомление
     * @param alert уведомление для проверки
     * @return уровень эскалации (0 - без эскалации, >0 - уровень эскалации)
     */
    @Cacheable(value = "escalationStates", key = "#alert.checkId + ':' + #alert.resourceUrl")
    public int checkEscalation(AlertMessage alert) {
        if (alert == null) {
            log.warn("Attempt to check escalation for null alert");
            return 0;
        }
        
        try {
            // Проверка, включена ли эскалация
            Boolean escalationEnabled = configurationService.isEscalationEnabled();
            if (escalationEnabled == null || !escalationEnabled) {
                log.debug("Escalation is disabled or not configured");
                return 0;
            }
            
            // Получение состояния эскалации для этого уведомления
            String alertKey = generateAlertKey(alert);
            if (alertKey == null || alertKey.isEmpty()) {
                log.warn("Cannot generate alert key for alert: {}", alert);
                return 0;
            }
            
            EscalationState state = escalationStates.computeIfAbsent(alertKey, k -> {
                log.info("Creating new escalation state for alert: {}", alertKey);
                return new EscalationState();
            });
            
            // Проверка условий эскалации
            int newEscalationLevel = determineEscalationLevel(alert, state);
            
            // Обновление состояния эскалации, если уровень изменился
            if (newEscalationLevel > state.getEscalationLevel()) {
                state.incrementEscalationLevel();
                log.info("Escalation level increased to {} for alert: {}", newEscalationLevel, alertKey);
            }
            
            return newEscalationLevel;
        } catch (Exception e) {
            log.error("Failed to check escalation for alert: checkId={}, resourceUrl={}",
                    alert.getCheckId(), alert.getResourceUrl(), e);
            return 0;
        }
    }
    
    /**
     * Определяет уровень эскалации для уведомления
     * @param alert уведомление
     * @param state состояние эскалации
     * @return уровень эскалации
     */
    private int determineEscalationLevel(AlertMessage alert, EscalationState state) {
        if (alert == null || state == null) {
            return 0;
        }
        
        try {
            int level = 0;
            LocalDateTime now = LocalDateTime.now();
            
            // Эскалация по времени бездействия (если проблема не решена в течение 30 минут)
            if (state.getFirstAlertTime().plusMinutes(30).isBefore(now)) {
                level = Math.max(level, 1);
                log.debug("Escalation level 1 triggered by downtime duration");
            }
            
            // Эскалация по времени с последней эскалации (если прошло более 1 часа)
            if (state.getLastEscalationTime().plusHours(1).isBefore(now)) {
                level = Math.max(level, state.getEscalationLevel() + 1);
                log.debug("Escalation level {} triggered by time since last escalation", level);
            }
            
            // Эскалация по типу ошибки (критические ошибки эскалируются сразу)
            if ("CRITICAL".equals(alert.getErrorMessage())) {
                level = Math.max(level, 2);
                log.debug("Escalation level 2 triggered by critical error message");
            }
            
            log.debug("Determined escalation level: {} for alert: checkId={}, resourceUrl={}",
                    level, alert.getCheckId(), alert.getResourceUrl());
            
            return level;
        } catch (Exception e) {
            log.error("Failed to determine escalation level for alert: checkId={}, resourceUrl={}",
                    alert.getCheckId(), alert.getResourceUrl(), e);
            return 0;
        }
    }
    
    /**
     * Генерирует ключ для идентификации уведомления
     * @param alert уведомление
     * @return ключ уведомления
     */
    private String generateAlertKey(AlertMessage alert) {
        if (alert == null) {
            return null;
        }
        
        // В реальной реализации можно использовать более сложную логику
        String checkId = alert.getCheckId();
        String resourceUrl = alert.getResourceUrl();
        
        if (checkId == null || checkId.isEmpty() || resourceUrl == null || resourceUrl.isEmpty()) {
            log.warn("Alert with null or empty checkId/resourceUrl: {}", alert);
            return null;
        }
        
        return checkId + ":" + resourceUrl;
    }
    
    /**
     * Сбрасывает состояние эскалации для уведомления
     * @param alert уведомление
     */
    @CacheEvict(value = "escalationStates", key = "#alert.checkId + ':' + #alert.resourceUrl")
    public void resetEscalation(AlertMessage alert) {
        if (alert == null) {
            log.warn("Attempt to reset escalation for null alert");
            return;
        }
        
        try {
            String alertKey = generateAlertKey(alert);
            if (alertKey == null || alertKey.isEmpty()) {
                log.warn("Cannot generate alert key for alert: {}", alert);
                return;
            }
            
            escalationStates.remove(alertKey);
            log.info("Escalation state reset for alert: {}", alertKey);
        } catch (Exception e) {
            log.error("Failed to reset escalation for alert: checkId={}, resourceUrl={}",
                    alert.getCheckId(), alert.getResourceUrl(), e);
        }
    }
}