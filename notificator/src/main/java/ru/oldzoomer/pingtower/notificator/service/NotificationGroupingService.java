package ru.oldzoomer.pingtower.notificator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.oldzoomer.pingtower.notificator.dto.AlertMessage;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationGroupingService {
    private final ConfigurationService configurationService;
    
    // Хранилище для групп уведомлений
    private final Map<String, NotificationGroup> notificationGroups = new ConcurrentHashMap<>();
    
    // Время последней очистки групп
    private LocalDateTime lastCleanup = LocalDateTime.now();
    
    public static class NotificationGroup {
        private final List<AlertMessage> alerts = new ArrayList<>();
        private final LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime lastUpdatedAt = LocalDateTime.now();
        
        public void addAlert(AlertMessage alert) {
            if (alert != null) {
                alerts.add(alert);
                lastUpdatedAt = LocalDateTime.now();
            }
        }
        
        public List<AlertMessage> getAlerts() {
            return new ArrayList<>(alerts);
        }
        
        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
        
        public LocalDateTime getLastUpdatedAt() {
            return lastUpdatedAt;
        }
        
        public int size() {
            return alerts.size();
        }
    }
    
    /**
     * Добавляет уведомление в группу
     * @param alert уведомление для добавления
     * @return true, если группа готова к отправке, false в противном случае
     */
    public boolean addAlertToGroup(AlertMessage alert) {
        if (alert == null) {
            log.warn("Attempt to add null alert to group");
            return false;
        }
        
        try {
            // Очистка старых групп
            cleanupOldGroups();
            
            // Генерация ключа группы на основе URL ресурса
            String groupKey = generateGroupKey(alert);
            if (groupKey == null || groupKey.isEmpty()) {
                log.warn("Cannot generate group key for alert: {}", alert);
                return false;
            }
            
            log.debug("Adding alert to group with key: {}", groupKey);
            
            // Получение или создание группы
            NotificationGroup group = notificationGroups.computeIfAbsent(groupKey, k -> {
                log.info("Creating new notification group with key: {}", groupKey);
                return new NotificationGroup();
            });
            
            // Добавление уведомления в группу
            group.addAlert(alert);
            log.debug("Added alert to group: {} (group size: {})", groupKey, group.size());
            
            // Проверка, готова ли группа к отправке
            boolean ready = isGroupReadyToSend(group);
            if (ready) {
                log.info("Group {} is ready to send (size: {})", groupKey, group.size());
            }
            
            return ready;
        } catch (Exception e) {
            log.error("Failed to add alert to group: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Получает готовые группы уведомлений для отправки
     * @return список готовых групп уведомлений
     */
    public List<NotificationGroup> getReadyGroups() {
        List<NotificationGroup> readyGroups = new ArrayList<>();
        
        try {
            log.debug("Checking for ready notification groups (total groups: {})", notificationGroups.size());
            
            for (Map.Entry<String, NotificationGroup> entry : notificationGroups.entrySet()) {
                if (isGroupReadyToSend(entry.getValue())) {
                    readyGroups.add(entry.getValue());
                }
            }
            
            log.info("Found {} ready notification groups", readyGroups.size());
            
            // Удаление отправленных групп
            readyGroups.forEach(group -> {
                notificationGroups.values().removeIf(g -> g == group);
            });
            
            return readyGroups;
        } catch (Exception e) {
            log.error("Failed to get ready groups: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Генерирует ключ группы на основе уведомления
     * @param alert уведомление
     * @return ключ группы
     */
    private String generateGroupKey(AlertMessage alert) {
        if (alert == null) {
            return null;
        }
        
        // В реальной реализации можно использовать более сложную логику группировки
        // Например, группировать по типу ошибки, домену и т.д.
        String resourceUrl = alert.getResourceUrl();
        if (resourceUrl == null || resourceUrl.isEmpty()) {
            log.warn("Alert with null or empty resourceUrl: {}", alert);
            return null;
        }
        
        return resourceUrl;
    }
    
    /**
     * Проверяет, готова ли группа к отправке
     * @param group группа уведомлений
     * @return true, если группа готова к отправке, false в противном случае
     */
    private boolean isGroupReadyToSend(NotificationGroup group) {
        if (group == null) {
            return false;
        }
        
        try {
            // Проверка по времени: если прошло больше интервала группировки
            Long groupingInterval = configurationService.getGroupingInterval();
            if (groupingInterval == null) {
                groupingInterval = 3000L; // Значение по умолчанию - 5 минут
                log.debug("Using default grouping interval: {} ms", groupingInterval);
            }
            
            LocalDateTime now = LocalDateTime.now();
            boolean timeReady = group.getLastUpdatedAt().plusSeconds(groupingInterval / 1000).isBefore(now);
            boolean sizeReady = group.size() >= 10; // или если в группе уже 10 уведомлений
            
            log.debug("Group ready check - timeReady: {}, sizeReady: {} (size: {})", timeReady, sizeReady, group.size());
            
            return timeReady || sizeReady;
        } catch (Exception e) {
            log.error("Failed to check if group is ready to send: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Очищает старые группы уведомлений
     */
    private void cleanupOldGroups() {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            // Очищаем раз в минуту
            if (lastCleanup.plusMinutes(1).isBefore(now)) {
                log.debug("Cleaning up old notification groups");
                lastCleanup = now;
                
                int beforeCount = notificationGroups.size();
                
                // Удаляем группы, которые не обновлялись более 10 минут
                notificationGroups.entrySet().removeIf(entry -> {
                    boolean shouldRemove = entry.getValue().getLastUpdatedAt().plusMinutes(10).isBefore(now);
                    if (shouldRemove) {
                        log.info("Removing old notification group: {}", entry.getKey());
                    }
                    return shouldRemove;
                });
                
                int afterCount = notificationGroups.size();
                log.info("Cleaned up notification groups: {} removed, {} remaining",
                        beforeCount - afterCount, afterCount);
            }
        } catch (Exception e) {
            log.error("Failed to cleanup old groups: {}", e.getMessage(), e);
        }
    }
}