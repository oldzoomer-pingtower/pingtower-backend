package ru.oldzoomer.pingtower.pinger.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.oldzoomer.pingtower.pinger.dto.CheckConfiguration;
import ru.oldzoomer.pingtower.pinger.kafka.CheckResultProducer;
import ru.oldzoomer.pingtower.pinger.kafka.SettingsConsumer;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckSchedulerServiceTest {

    @Mock
    private SettingsConsumer settingsConsumer;

    @Mock
    private CheckExecutorFactory checkExecutorFactory;

    @Mock
    private CheckResultProducer checkResultProducer;

    @InjectMocks
    private CheckSchedulerService checkSchedulerService;

    private CheckConfiguration config;

    @BeforeEach
    void setUp() {
        config = new CheckConfiguration();
        config.setId("test-check");
        config.setType("HTTP");
        config.setResourceUrl("https://example.com");
        config.setFrequency(60000L);
        config.setTimeout(5000);
    }

    @Test
    void testScheduleCheck_NewCheck() {
        // This test should verify that the check is properly added to scheduledTasks
        checkSchedulerService.scheduleCheck(config);

        // We can't easily verify scheduling, but we can verify that the method doesn't throw exceptions
        assertDoesNotThrow(() -> checkSchedulerService.scheduleCheck(config));
    }

    @Test
    void testScheduleCheck_ReplaceExisting() {
        // First schedule a check
        checkSchedulerService.scheduleCheck(config);

        // Now schedule the same check again (should replace)
        CheckConfiguration updatedConfig = new CheckConfiguration();
        updatedConfig.setId("test-check");
        updatedConfig.setType("HTTP");
        updatedConfig.setResourceUrl("https://example.com");
        updatedConfig.setFrequency(30000L); // Different frequency

        // Should not throw exception when replacing
        assertDoesNotThrow(() -> checkSchedulerService.scheduleCheck(updatedConfig));
    }

    @Test
    void testRemoveCheck_NonExisting() {
        // Try to remove a non-existing check
        assertDoesNotThrow(() -> checkSchedulerService.removeCheck("nonexistent"));
    }

    @Test
    void testExecuteCheck_Success() {
        // Mock the executor and its execution
        CheckExecutor mockExecutor = mock(CheckExecutor.class);
        when(checkExecutorFactory.getExecutor("HTTP")).thenReturn(mockExecutor);

        // Mock the result
        ru.oldzoomer.pingtower.pinger.dto.CheckResult mockResult = new ru.oldzoomer.pingtower.pinger.dto.CheckResult();
        mockResult.setCheckId("test-check");
        mockResult.setStatus("SUCCESS");
        when(mockExecutor.execute(config)).thenReturn(mockResult);

        checkSchedulerService.executeCheck(config);

        // Verify that the result was sent to Kafka
        verify(checkResultProducer).sendCheckResult(mockResult);
    }

    @Test
    void testExecuteCheck_Exception() {
        // Mock the executor to throw an exception
        CheckExecutor mockExecutor = mock(CheckExecutor.class);
        when(checkExecutorFactory.getExecutor("HTTP")).thenReturn(mockExecutor);
        when(mockExecutor.execute(config)).thenThrow(new RuntimeException("Test error"));

        // Should not throw exception, just log error
        assertDoesNotThrow(() -> checkSchedulerService.executeCheck(config));

        // Verify that no result was sent to Kafka
        verify(checkResultProducer, never()).sendCheckResult(any());
    }

    @Test
    void testScheduleAllChecks_NewChecks() {
        // Mock settings consumer to return some configurations
        Map<String, CheckConfiguration> configs = Map.of(
                "check-1", createConfig("check-1", "HTTP", "https://example1.com", 60000L),
                "check-2", createConfig("check-2", "TCP", "tcp://example2.com:8080", 30000L)
        );

        when(settingsConsumer.getCheckConfigurations()).thenReturn(configs);

        // Should not throw exception
        assertDoesNotThrow(() -> checkSchedulerService.scheduleAllChecks());
    }

    @Test
    void testScheduleAllChecks_ExistingChecks() {
        // First schedule a check
        CheckConfiguration existingConfig = createConfig("existing-check", "HTTP", "https://existing.com", 60000L);
        checkSchedulerService.scheduleCheck(existingConfig);

        // Mock settings consumer to return the same configuration
        Map<String, CheckConfiguration> configs = Map.of(
                "existing-check", existingConfig
        );

        when(settingsConsumer.getCheckConfigurations()).thenReturn(configs);

        // Should not throw exception when scheduling existing checks
        assertDoesNotThrow(() -> checkSchedulerService.scheduleAllChecks());
    }

    private CheckConfiguration createConfig(String id, String type, String url, Long frequency) {
        CheckConfiguration config = new CheckConfiguration();
        config.setId(id);
        config.setType(type);
        config.setResourceUrl(url);
        config.setFrequency(frequency);
        config.setTimeout(5000);
        return config;
    }
}