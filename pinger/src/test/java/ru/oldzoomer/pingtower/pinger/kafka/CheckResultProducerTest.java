package ru.oldzoomer.pingtower.pinger.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import ru.oldzoomer.pingtower.pinger.dto.CheckResult;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckResultProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private CheckResultProducer checkResultProducer;

    private CheckResult testResult;

    @BeforeEach
    void setUp() {
        testResult = new CheckResult();
        testResult.setCheckId("test-check-1");
        testResult.setResourceUrl("https://example.com");
        testResult.setTimestamp(LocalDateTime.now());
        testResult.setStatus("SUCCESS");
        testResult.setResponseTime(150L);
        testResult.setHttpStatusCode(200);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testSendCheckResult_Success() {
        // Mock the Kafka template to return a successful future
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));
        
        when(kafkaTemplate.send(eq("pingtower.check.results"), eq("test-check-1"), any(CheckResult.class))).thenReturn(future);
        
        checkResultProducer.sendCheckResult(testResult);
        
        // Verify that the send method was called with the correct parameters
        verify(kafkaTemplate).send(eq("pingtower.check.results"), eq("test-check-1"), eq(testResult));
    }

    @Test
    void testSendCheckResult_Exception() {
        // Mock the Kafka template to throw an exception
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka error"));
        
        when(kafkaTemplate.send(eq("pingtower.check.results"), eq("test-check-1"), any(CheckResult.class))).thenReturn(future);
        
        // Should not throw exception, just log error
        assertDoesNotThrow(() -> checkResultProducer.sendCheckResult(testResult));
        
        // Verify that the send method was called
        verify(kafkaTemplate).send(eq("pingtower.check.results"), eq("test-check-1"), any(CheckResult.class));
    }

    @Test
    void testSendCheckResult_NullResult() {
        // Should not throw exception when result is null
        assertDoesNotThrow(() -> checkResultProducer.sendCheckResult(null));
        
        // Verify that the send method was not called
        verify(kafkaTemplate, never()).send(anyString(), any(), any());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testSendCheckResult_NullCheckId() {
        testResult.setCheckId(null);
        
        // Mock the Kafka template to return a successful future
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));
        
        when(kafkaTemplate.send(eq("pingtower.check.results"), isNull(), any(CheckResult.class))).thenReturn(future);
        
        // Should not throw exception when checkId is null
        assertDoesNotThrow(() -> checkResultProducer.sendCheckResult(testResult));
        
        // Verify that the send method was called with null key
        verify(kafkaTemplate).send(eq("pingtower.check.results"), isNull(), eq(testResult));
    }
}