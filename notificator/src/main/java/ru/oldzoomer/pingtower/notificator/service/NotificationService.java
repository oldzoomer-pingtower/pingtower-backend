package ru.oldzoomer.pingtower.notificator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.oldzoomer.pingtower.notificator.dto.AlertMessage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    // private final ConfigurationService configurationService;
    private final NotificationGroupingService notificationGroupingService;
    private final EscalationService escalationService;
    private final EmailNotificationService emailNotificationService;
    private final TelegramNotificationService telegramNotificationService;
    private final WebhookNotificationService webhookNotificationService;
    
    /**
     * Обрабатывает входящее уведомление
     * @param alert уведомление для обработки
     */
    public void processAlert(AlertMessage alert) {
        if (alert == null) {
            log.warn("Attempt to process null alert");
            return;
        }
        
        try {
            log.info("Processing alert: checkId={}, resourceUrl={}, status={}",
                    alert.getCheckId(), alert.getResourceUrl(), alert.getStatus());
            
            // Проверка обязательных полей
            if (alert.getCheckId() == null || alert.getCheckId().isEmpty()) {
                log.warn("Alert with null or empty checkId: {}", alert);
                return;
            }
            
            if (alert.getResourceUrl() == null || alert.getResourceUrl().isEmpty()) {
                log.warn("Alert with null or empty resourceUrl: {}", alert);
                return;
            }
            
            // Добавляем уведомление в группу
            boolean groupReady = notificationGroupingService.addAlertToGroup(alert);
            
            // Если группа готова к отправке, отправляем уведомления
            if (groupReady) {
                sendGroupNotifications(alert);
            } else {
                // Если группа еще не готова, отправляем индивидуальное уведомление
                sendIndividualNotification(alert);
            }
        } catch (Exception e) {
            log.error("Failed to process alert: checkId={}, resourceUrl={}",
                    alert.getCheckId(), alert.getResourceUrl(), e);
        }
    }
    
    /**
     * Отправляет групповые уведомления
     * @param alert уведомление, которое инициировало отправку группы
     */
    private void sendGroupNotifications(AlertMessage alert) {
        try {
            List<NotificationGroupingService.NotificationGroup> readyGroups =
                    notificationGroupingService.getReadyGroups();
            
            if (readyGroups.isEmpty()) {
                log.debug("No ready groups to send notifications for alert: checkId={}", alert.getCheckId());
                return;
            }
            
            log.info("Sending notifications for {} ready groups", readyGroups.size());
            
            for (NotificationGroupingService.NotificationGroup group : readyGroups) {
                // Формируем сообщение для группы
                String message = formatGroupMessage(group);
                
                if (message != null && !message.isEmpty()) {
                    // Отправляем уведомление через все доступные каналы
                    sendNotification(message);
                } else {
                    log.warn("Skipping empty group message for group with {} alerts", group.size());
                }
            }
        } catch (Exception e) {
            log.error("Failed to send group notifications for alert: checkId={}", alert.getCheckId(), e);
        }
    }
    
    /**
     * Отправляет индивидуальное уведомление
     * @param alert уведомление для отправки
     */
    private void sendIndividualNotification(AlertMessage alert) {
        try {
            // Проверяем необходимость эскалации
            int escalationLevel = escalationService.checkEscalation(alert);
            
            // Формируем сообщение
            String message = formatIndividualMessage(alert, escalationLevel);
            
            if (message != null && !message.isEmpty()) {
                // Отправляем уведомление через все доступные каналы
                sendNotification(message);
            } else {
                log.warn("Skipping empty individual message for alert: checkId={}", alert.getCheckId());
            }
        } catch (Exception e) {
            log.error("Failed to send individual notification for alert: checkId={}", alert.getCheckId(), e);
        }
    }
    
    /**
     * Отправляет уведомление через все доступные каналы
     * @param message сообщение для отправки
     */
    private void sendNotification(String message) {
        if (message == null || message.isEmpty()) {
            log.warn("Attempt to send empty notification");
            return;
        }
        
        try {
            log.info("Sending notification through all channels");
            
            // Отправка через email
            emailNotificationService.send(message);
            
            // Отправка через Telegram
            telegramNotificationService.send(message);
            
            // Отправка через webhook
            webhookNotificationService.send(message);
            
            log.info("Notification sent successfully through all channels");
        } catch (Exception e) {
            log.error("Failed to send notification through channels: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Форматирует сообщение для группы уведомлений
     * @param group группа уведомлений
     * @return отформатированное сообщение
     */
    private String formatGroupMessage(NotificationGroupingService.NotificationGroup group) {
        if (group == null || group.size() == 0) {
            log.warn("Attempt to format message for null or empty group");
            return null;
        }
        
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("Grouped notifications:\n");
            sb.append("Total alerts: ").append(group.size()).append("\n");
            sb.append("Created at: ").append(group.getCreatedAt()).append("\n");
            sb.append("Resources with issues:\n");
            
            for (AlertMessage alert : group.getAlerts()) {
                if (alert != null && alert.getResourceUrl() != null) {
                    sb.append("- ").append(alert.getResourceUrl())
                      .append(" (").append(alert.getErrorMessage()).append(")\n");
                }
            }
            
            return sb.toString();
        } catch (Exception e) {
            log.error("Failed to format group message: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Форматирует индивидуальное сообщение
     * @param alert уведомление
     * @param escalationLevel уровень эскалации
     * @return отформатированное сообщение
     */
    private String formatIndividualMessage(AlertMessage alert, int escalationLevel) {
        if (alert == null) {
            log.warn("Attempt to format message for null alert");
            return null;
        }
        
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("Resource alert:\n");
            sb.append("Resource URL: ").append(alert.getResourceUrl()).append("\n");
            sb.append("Status: ").append(alert.getStatus()).append("\n");
            sb.append("Error message: ").append(alert.getErrorMessage()).append("\n");
            sb.append("Downtime duration: ").append(alert.getDowntimeDuration()).append(" ms\n");
            
            if (escalationLevel > 0) {
                sb.append("Escalation level: ").append(escalationLevel).append("\n");
            }
            
            return sb.toString();
        } catch (Exception e) {
            log.error("Failed to format individual message: {}", e.getMessage(), e);
            return null;
        }
    }
}