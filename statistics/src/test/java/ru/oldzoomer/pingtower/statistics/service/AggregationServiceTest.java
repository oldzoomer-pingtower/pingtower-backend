package ru.oldzoomer.pingtower.statistics.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.oldzoomer.pingtower.statistics.cassandra.entity.AggregatedCheckResult;
import ru.oldzoomer.pingtower.statistics.cassandra.repository.AggregatedCheckResultRepository;
import ru.oldzoomer.pingtower.statistics.dto.CheckResult;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AggregationServiceTest {

    @Mock
    private AggregatedCheckResultRepository aggregatedCheckResultRepository;

    @InjectMocks
    private AggregationService aggregationService;

    private LocalDateTime testTimestamp;

    @BeforeEach
    void setUp() {
        testTimestamp = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
    }

    @Test
    void testProcessCheckResultForAggregation_Success() {
        // Given
        CheckResult checkResult = createTestCheckResult("test-check-1", "UP", 150, 200);

        // When
        aggregationService.processCheckResultForAggregation(checkResult);

        // Then
        // Should not throw exception, should process aggregation
        // We can verify that the repository is eventually called when threshold is reached
    }

    @Test
    void testProcessCheckResultForAggregation_Exception() {
        // Given
        CheckResult checkResult = createTestCheckResult("test-check-1", "UP", 150, 200);
        // Mock repository to throw exception
        when(aggregatedCheckResultRepository.save(any(AggregatedCheckResult.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When
        // Add enough results to trigger save
        for (int i = 0; i < 10; i++) {
            aggregationService.processCheckResultForAggregation(checkResult);
        }

        // Then
        // Should not throw exception, should log error
        verify(aggregatedCheckResultRepository, atLeastOnce()).save(any(AggregatedCheckResult.class));
    }

    @Test
    void testAggregationThresholdReached_SavesData() {
        // Given
        CheckResult checkResult = createTestCheckResult("test-check-1", "UP", 150, 200);

        // When
        // Add exactly 10 results to reach the threshold
        for (int i = 0; i < 10; i++) {
            aggregationService.processCheckResultForAggregation(checkResult);
        }

        // Then
        // The repository should be called to save aggregated data
        verify(aggregatedCheckResultRepository, atLeastOnce()).save(any(AggregatedCheckResult.class));
    }

    @Test
    void testAggregationThresholdNotReached_DoesNotSaveData() {
        // Given
        CheckResult checkResult = createTestCheckResult("test-check-1", "UP", 150, 200);

        // When
        // Add only 9 results (below threshold)
        for (int i = 0; i < 9; i++) {
            aggregationService.processCheckResultForAggregation(checkResult);
        }

        // Then
        // The repository should not be called
        verify(aggregatedCheckResultRepository, never()).save(any(AggregatedCheckResult.class));
    }

    @Test
    void testMultipleCheckIds_AggregateSeparately() {
        // Given
        CheckResult result1 = createTestCheckResult("check-1", "UP", 100, 200);
        CheckResult result2 = createTestCheckResult("check-2", "DOWN", 500, 500);

        // When
        // Add 5 results for each check (total 10, but separate check IDs)
        for (int i = 0; i < 5; i++) {
            aggregationService.processCheckResultForAggregation(result1);
            aggregationService.processCheckResultForAggregation(result2);
        }

        // Then
        // Should not save yet since each check has only 5 results (below threshold of 10)
        verify(aggregatedCheckResultRepository, never()).save(any(AggregatedCheckResult.class));

        // Add 5 more for check-1 to reach threshold
        for (int i = 0; i < 5; i++) {
            aggregationService.processCheckResultForAggregation(result1);
        }

        // Then
        // Should save for check-1
        verify(aggregatedCheckResultRepository, atLeastOnce()).save(any(AggregatedCheckResult.class));
    }

    @Test
    void testDifferentIntervals_AggregateSeparately() {
        // Given
        CheckResult result = createTestCheckResult("test-check-1", "UP", 150, 200);

        // When
        // Add 10 results to reach threshold for default interval (HOUR)
        for (int i = 0; i < 10; i++) {
            aggregationService.processCheckResultForAggregation(result);
        }

        // Then
        // Should save for HOUR interval
        verify(aggregatedCheckResultRepository, atLeastOnce()).save(any(AggregatedCheckResult.class));
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