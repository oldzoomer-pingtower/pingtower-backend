package ru.oldzoomer.pingtower.notificator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailNotificationServiceTest {

    @Mock
    private ConfigurationService configurationService;

    @InjectMocks
    private EmailNotificationService emailNotificationService;

    @Test
    void testSend_NullMessage() {
        emailNotificationService.send(null);
        verifyNoInteractions(configurationService);
    }

    @Test
    void testSend_EmptyMessage() {
        emailNotificationService.send("");
        verifyNoInteractions(configurationService);
    }

    @Test
    void testSend_ValidMessage_NoRecipientConfigured() {
        when(configurationService.getStringSetting("email.to.address")).thenReturn(null);
        
        emailNotificationService.send("Test message");
        
        verify(configurationService).getStringSetting("email.to.address");
        verifyNoMoreInteractions(configurationService);
    }

    @Test
    void testSend_ValidMessage_EmptyRecipient() {
        when(configurationService.getStringSetting("email.to.address")).thenReturn("");
        
        emailNotificationService.send("Test message");
        
        verify(configurationService).getStringSetting("email.to.address");
        verifyNoMoreInteractions(configurationService);
    }

    @Test
    void testSend_ValidMessage_RecipientConfigured() {
        when(configurationService.getStringSetting("email.to.address")).thenReturn("admin@example.com");
        
        emailNotificationService.send("Test message");
        
        verify(configurationService).getStringSetting("email.to.address");
        // In real implementation, would verify email sending logic
    }

    @Test
    void testSend_ExceptionHandling() {
        when(configurationService.getStringSetting(anyString())).thenThrow(new RuntimeException("Config error"));
        
        emailNotificationService.send("Test message");
        
        // Should not throw exception, just log error
        verify(configurationService).getStringSetting("email.to.address");
    }

    @Test
    void testGetChannelType() {
        String channelType = emailNotificationService.getChannelType();
        assertEquals("EMAIL", channelType);
    }
}