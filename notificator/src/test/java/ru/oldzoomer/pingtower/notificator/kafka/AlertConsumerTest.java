package ru.oldzoomer.pingtower.notificator.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.oldzoomer.pingtower.notificator.dto.AlertMessage;
import ru.oldzoomer.pingtower.notificator.service.NotificationService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertConsumerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AlertConsumer alertConsumer;

    @Test
    void testConsumeAlert_NullMessage() {
        alertConsumer.consumeAlert(null, "pingtower.check.alerts", 0, 1L);
        
        verifyNoInteractions(notificationService);
    }

    @Test
    void testConsumeAlert_EmptyCheckId() {
        AlertMessage alert = new AlertMessage();
        alert.setCheckId("");
        alert.setResourceUrl("https://example.com");
        alert.setTimestamp(LocalDateTime.now());
        
        alertConsumer.consumeAlert(alert, "pingtower.check.alerts", 0, 1L);
        
        verifyNoInteractions(notificationService);
    }

    @Test
    void testConsumeAlert_ValidMessage() {
        AlertMessage alert = new AlertMessage();
        alert.setCheckId("check-123");
        alert.setResourceUrl("https://example.com");
        alert.setTimestamp(LocalDateTime.now());
        alert.setStatus("DOWN");
        alert.setErrorMessage("Connection timeout");
        
        alertConsumer.consumeAlert(alert, "pingtower.check.alerts", 0, 1L);
        
        verify(notificationService).processAlert(alert);
    }

    @Test
    void testConsumeAlert_ExceptionInProcessing() {
        AlertMessage alert = new AlertMessage();
        alert.setCheckId("check-123");
        alert.setResourceUrl("https://example.com");
        alert.setTimestamp(LocalDateTime.now());
        
        doThrow(new RuntimeException("Processing error")).when(notificationService).processAlert(any(AlertMessage.class));
        
        alertConsumer.consumeAlert(alert, "pingtower.check.alerts", 0, 1L);
        
        // Should not throw exception, just log error
        verify(notificationService).processAlert(alert);
    }

    @Test
    void testConsumeAlert_NullResourceUrl() {
        AlertMessage alert = new AlertMessage();
        alert.setCheckId("check-123");
        alert.setResourceUrl(null);
        alert.setTimestamp(LocalDateTime.now());
        
        alertConsumer.consumeAlert(alert, "pingtower.check.alerts", 0, 1L);
        
        // Should still process the alert since only checkId is mandatory
        verify(notificationService).processAlert(alert);
    }
}