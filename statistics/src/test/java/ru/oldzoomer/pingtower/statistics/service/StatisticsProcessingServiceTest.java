package ru.oldzoomer.pingtower.statistics.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.oldzoomer.pingtower.statistics.cassandra.entity.RawCheckResult;
import ru.oldzoomer.pingtower.statistics.cassandra.repository.RawCheckResultRepository;
import ru.oldzoomer.pingtower.statistics.dto.CheckResult;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsProcessingServiceTest {

    @Mock
    private RawCheckResultRepository rawCheckResultRepository;

    @Mock
    private AggregationService aggregationService;

    @InjectMocks
    private StatisticsProcessingService statisticsProcessingService;

    private LocalDateTime testTimestamp;

    @BeforeEach
    void setUp() {
        testTimestamp = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
    }

    @Test
    void testProcessCheckResult_Success() {
        // Given
        CheckResult checkResult = createTestCheckResult("test-check-1", "UP", 150, 200);

        // When
        statisticsProcessingService.processCheckResult(checkResult);

        // Then
        verify(rawCheckResultRepository, times(1)).save(any(RawCheckResult.class));
        verify(aggregationService, times(1)).processCheckResultForAggregation(checkResult);
    }

    @Test
    void testProcessCheckResult_WithMetrics() {
        // Given
        CheckResult checkResult = createTestCheckResult("test-check-1", "UP", 150, 200);
        CheckResult.Metrics metrics = new CheckResult.Metrics();
        metrics.setConnectionTime(50L);
        metrics.setTimeToFirstByte(100L);
        metrics.setSslValid(true);
        metrics.setSslExpirationDate(LocalDateTime.of(2024, 12, 31, 23, 59, 59));
        checkResult.setMetrics(metrics);

        // When
        statisticsProcessingService.processCheckResult(checkResult);

        // Then
        verify(rawCheckResultRepository, times(1)).save(any(RawCheckResult.class));
        verify(aggregationService, times(1)).processCheckResultForAggregation(checkResult);
    }

    @Test
    void testProcessCheckResult_ExceptionDuringSave() {
        // Given
        CheckResult checkResult = createTestCheckResult("test-check-1", "UP", 150, 200);
        when(rawCheckResultRepository.save(any(RawCheckResult.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When
        statisticsProcessingService.processCheckResult(checkResult);

        // Then
        // Should not throw exception, should log error
        verify(rawCheckResultRepository, times(1)).save(any(RawCheckResult.class));
        verify(aggregationService, times(1)).processCheckResultForAggregation(checkResult);
    }

    @Test
    void testProcessCheckResult_ExceptionDuringAggregation() {
        // Given
        CheckResult checkResult = createTestCheckResult("test-check-1", "UP", 150, 200);
        doThrow(new RuntimeException("Aggregation error"))
                .when(aggregationService).processCheckResultForAggregation(checkResult);

        // When
        statisticsProcessingService.processCheckResult(checkResult);

        // Then
        // Should not throw exception, should log error
        verify(rawCheckResultRepository, times(1)).save(any(RawCheckResult.class));
        verify(aggregationService, times(1)).processCheckResultForAggregation(checkResult);
    }

    @Test
    void testSaveRawCheckResult_Success() {
        // Given
        CheckResult checkResult = createTestCheckResult("test-check-1", "UP", 150, 200);

        // When
        // Using reflection to test private method, but we'll test through public method
        statisticsProcessingService.processCheckResult(checkResult);

        // Then
        verify(rawCheckResultRepository, times(1)).save(any(RawCheckResult.class));
    }

    @Test
    void testSaveRawCheckResult_WithMetrics() {
        // Given
        CheckResult checkResult = createTestCheckResult("test-check-1", "UP", 150, 200);
        CheckResult.Metrics metrics = new CheckResult.Metrics();
        metrics.setConnectionTime(50L);
        metrics.setTimeToFirstByte(100L);
        metrics.setSslValid(true);
        metrics.setSslExpirationDate(LocalDateTime.of(2024, 12, 31, 23, 59, 59));
        checkResult.setMetrics(metrics);

        // When
        statisticsProcessingService.processCheckResult(checkResult);

        // Then
        verify(rawCheckResultRepository, times(1)).save(any(RawCheckResult.class));
    }

    @Test
    void testSaveRawCheckResult_WithNullMetrics() {
        // Given
        CheckResult checkResult = createTestCheckResult("test-check-1", "UP", 150, 200);
        checkResult.setMetrics(null);

        // When
        statisticsProcessingService.processCheckResult(checkResult);

        // Then
        verify(rawCheckResultRepository, times(1)).save(any(RawCheckResult.class));
    }

    @Test
    void testSaveRawCheckResult_WithPartialMetrics() {
        // Given
        CheckResult checkResult = createTestCheckResult("test-check-1", "UP", 150, 200);
        CheckResult.Metrics metrics = new CheckResult.Metrics();
        metrics.setConnectionTime(50L);
        metrics.setTimeToFirstByte(100L);
        // sslValid and sslExpirationDate are null
        checkResult.setMetrics(metrics);

        // When
        statisticsProcessingService.processCheckResult(checkResult);

        // Then
        verify(rawCheckResultRepository, times(1)).save(any(RawCheckResult.class));
    }

    private CheckResult createTestCheckResult(String checkId, String status, long responseTime, int httpStatusCode) {
        CheckResult checkResult = new CheckResult();
        checkResult.setCheckId(checkId);
        checkResult.setTimestamp(testTimestamp);
        checkResult.setStatus(status);
        checkResult.setResponseTime(responseTime);
        checkResult.setHttpStatusCode(httpStatusCode);
        checkResult.setErrorMessage(null);
        return checkResult;
    }
}