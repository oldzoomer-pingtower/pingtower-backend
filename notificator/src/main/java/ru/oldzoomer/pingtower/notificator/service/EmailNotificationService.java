package ru.oldzoomer.pingtower.notificator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationService implements NotificationChannel {
    // private final JavaMailSender mailSender;
    private final ConfigurationService configurationService;

    @Override
    public void send(String message) {
        if (message == null || message.isEmpty()) {
            log.warn("Attempt to send empty email notification");
            return;
        }
        
        try {
            log.info("Sending email notification: {}", message);
            
            // В реальной реализации здесь будет логика отправки email
            // с использованием настроек из ConfigurationService
            String toAddress = configurationService.getStringSetting("email.to.address");
            if (toAddress == null || toAddress.isEmpty()) {
                log.warn("Email recipient address not configured");
                return;
            }
            
            // Пример отправки email (закомментирован для демонстрации)
            /*
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(toAddress);
            mailMessage.setSubject("PingTower Notification");
            mailMessage.setText(message);
            mailSender.send(mailMessage);
            log.info("Email notification sent successfully to {}", toAddress);
            */
        } catch (Exception e) {
            log.error("Failed to send email notification: {}", e.getMessage(), e);
        }
    }

    @Override
    public String getChannelType() {
        return "EMAIL";
    }
}