package ru.oldzoomer.pingtower.notificator.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.oldzoomer.pingtower.notificator.service.ConfigurationService;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettingsConsumerTest {

    @Mock
    private ConfigurationService configurationService;

    @InjectMocks
    private SettingsConsumer settingsConsumer;

    @Test
    void testConsumeSettingsUpdate_NullMessage() {
        settingsConsumer.consumeSettingsUpdate(null, "pingtower.settings.updates", 0, 1L);
        
        verifyNoInteractions(configurationService);
    }

    @Test
    void testConsumeSettingsUpdate_EmptyModule() {
        Map<String, Object> settingsUpdate = Map.of(
            "module", "",
            "key", "email.to.address",
            "value", "admin@example.com"
        );
        
        settingsConsumer.consumeSettingsUpdate(settingsUpdate, "pingtower.settings.updates", 0, 1L);
        
        verifyNoInteractions(configurationService);
    }

    @Test
    void testConsumeSettingsUpdate_NullModule() {
        Map<String, Object> settingsUpdate = new HashMap<>();
        settingsUpdate.put("module", null);
        settingsUpdate.put("key", "email.to.address");
        settingsUpdate.put("value", "admin@example.com");
        
        settingsConsumer.consumeSettingsUpdate(settingsUpdate, "pingtower.settings.updates", 0, 1L);
        
        verifyNoInteractions(configurationService);
    }

    @Test
    void testConsumeSettingsUpdate_NotificatorModule_ValidKey() {
        Map<String, Object> settingsUpdate = Map.of(
            "module", "notificator",
            "key", "email.to.address",
            "value", "admin@example.com",
            "settingId", "setting-123",
            "action", "UPDATE"
        );
        
        settingsConsumer.consumeSettingsUpdate(settingsUpdate, "pingtower.settings.updates", 0, 1L);
        
        verify(configurationService).updateSetting("email.to.address", "admin@example.com");
    }

    @Test
    void testConsumeSettingsUpdate_NotificatorModule_EmptyKey() {
        Map<String, Object> settingsUpdate = Map.of(
            "module", "notificator",
            "key", "",
            "value", "admin@example.com"
        );
        
        settingsConsumer.consumeSettingsUpdate(settingsUpdate, "pingtower.settings.updates", 0, 1L);
        
        verifyNoInteractions(configurationService);
    }

    @Test
    void testConsumeSettingsUpdate_NotificatorModule_NullKey() {
        Map<String, Object> settingsUpdate = new HashMap<>();
        settingsUpdate.put("module", "notificator");
        settingsUpdate.put("key", null);
        settingsUpdate.put("value", "admin@example.com");
        
        settingsConsumer.consumeSettingsUpdate(settingsUpdate, "pingtower.settings.updates", 0, 1L);
        
        verifyNoInteractions(configurationService);
    }

    @Test
    void testConsumeSettingsUpdate_OtherModule() {
        Map<String, Object> settingsUpdate = Map.of(
            "module", "pinger",
            "key", "check.interval",
            "value", 30000
        );
        
        settingsConsumer.consumeSettingsUpdate(settingsUpdate, "pingtower.settings.updates", 0, 1L);
        
        verifyNoInteractions(configurationService);
    }

    @Test
    void testConsumeSettingsUpdate_ExceptionInProcessing() {
        Map<String, Object> settingsUpdate = Map.of(
            "module", "notificator",
            "key", "email.to.address",
            "value", "admin@example.com"
        );
        
        doThrow(new RuntimeException("Update error")).when(configurationService).updateSetting(any(), any());
        
        settingsConsumer.consumeSettingsUpdate(settingsUpdate, "pingtower.settings.updates", 0, 1L);
        
        // Should not throw exception, just log error
        verify(configurationService).updateSetting("email.to.address", "admin@example.com");
    }

    @Test
    void testConsumeSettingsUpdate_MissingKeyField() {
        Map<String, Object> settingsUpdate = Map.of(
            "module", "notificator",
            "value", "admin@example.com"
        );
        
        settingsConsumer.consumeSettingsUpdate(settingsUpdate, "pingtower.settings.updates", 0, 1L);
        
        verifyNoInteractions(configurationService);
    }

    @Test
    void testConsumeSettingsUpdate_MissingValueField() {
        Map<String, Object> settingsUpdate = Map.of(
            "module", "notificator",
            "key", "email.to.address"
        );
        
        settingsConsumer.consumeSettingsUpdate(settingsUpdate, "pingtower.settings.updates", 0, 1L);
        
        verify(configurationService).updateSetting("email.to.address", null);
    }
}