package ru.oldzoomer.pingtower.statistics.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.oldzoomer.pingtower.statistics.cassandra.entity.AggregatedCheckResult;
import ru.oldzoomer.pingtower.statistics.cassandra.entity.RawCheckResult;
import ru.oldzoomer.pingtower.statistics.cassandra.repository.AggregatedCheckResultRepository;
import ru.oldzoomer.pingtower.statistics.cassandra.repository.RawCheckResultRepository;
import ru.oldzoomer.pingtower.statistics.dto.CheckResult;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsRetrievalServiceTest {

    @Mock
    private RawCheckResultRepository rawCheckResultRepository;

    @Mock
    private AggregatedCheckResultRepository aggregatedCheckResultRepository;

    @InjectMocks
    private StatisticsRetrievalService statisticsRetrievalService;

    private LocalDateTime testTimestamp;

    @BeforeEach
    void setUp() {
        testTimestamp = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
    }

    @Test
    void testGetLatestCheckResult_WithResults() {
        // Given
        String checkId = "test-check-1";
        RawCheckResult rawCheckResult = createTestRawCheckResult(checkId, "UP", 150, 200);
        
        when(rawCheckResultRepository.findByKeyCheckIdOrderByKeyTimestampDesc(checkId))
                .thenReturn(List.of(rawCheckResult));

        // When
        CheckResult result = statisticsRetrievalService.getLatestCheckResult(checkId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCheckId()).isEqualTo(checkId);
        assertThat(result.getStatus()).isEqualTo("UP");
        assertThat(result.getResponseTime()).isEqualTo(150);
        assertThat(result.getHttpStatusCode()).isEqualTo(200);
    }

    @Test
    void testGetLatestCheckResult_NoResults() {
        // Given
        String checkId = "test-check-1";
        when(rawCheckResultRepository.findByKeyCheckIdOrderByKeyTimestampDesc(checkId))
                .thenReturn(Collections.emptyList());

        // When
        CheckResult result = statisticsRetrievalService.getLatestCheckResult(checkId);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void testGetLatestCheckResult_Exception() {
        // Given
        String checkId = "test-check-1";
        when(rawCheckResultRepository.findByKeyCheckIdOrderByKeyTimestampDesc(checkId))
                .thenThrow(new RuntimeException("Database error"));

        // When
        CheckResult result = statisticsRetrievalService.getLatestCheckResult(checkId);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void testGetCheckHistory_WithResults() {
        // Given
        String checkId = "test-check-1";
        LocalDateTime from = testTimestamp.minusHours(1);
        LocalDateTime to = testTimestamp;
        
        RawCheckResult result1 = createTestRawCheckResult(checkId, "UP", 100, 200);
        RawCheckResult result2 = createTestRawCheckResult(checkId, "DOWN", 500, 500);
        
        when(rawCheckResultRepository.findByKeyCheckIdAndKeyTimestampBetween(checkId, from, to))
                .thenReturn(List.of(result1, result2));

        // When
        List<CheckResult> results = statisticsRetrievalService.getCheckHistory(checkId, from, to, 10, 0);

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getStatus()).isEqualTo("UP");
        assertThat(results.get(1).getStatus()).isEqualTo("DOWN");
    }

    @Test
    void testGetCheckHistory_WithPagination() {
        // Given
        String checkId = "test-check-1";
        LocalDateTime from = testTimestamp.minusHours(1);
        LocalDateTime to = testTimestamp;
        
        RawCheckResult result1 = createTestRawCheckResult(checkId, "UP", 100, 200);
        RawCheckResult result2 = createTestRawCheckResult(checkId, "DOWN", 500, 500);
        RawCheckResult result3 = createTestRawCheckResult(checkId, "UP", 150, 200);
        
        when(rawCheckResultRepository.findByKeyCheckIdAndKeyTimestampBetween(checkId, from, to))
                .thenReturn(List.of(result1, result2, result3));

        // When - limit 2, offset 1
        List<CheckResult> results = statisticsRetrievalService.getCheckHistory(checkId, from, to, 2, 1);

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getStatus()).isEqualTo("DOWN");
        assertThat(results.get(1).getStatus()).isEqualTo("UP");
    }

    @Test
    void testGetCheckHistory_NoResults() {
        // Given
        String checkId = "test-check-1";
        LocalDateTime from = testTimestamp.minusHours(1);
        LocalDateTime to = testTimestamp;
        
        when(rawCheckResultRepository.findByKeyCheckIdAndKeyTimestampBetween(checkId, from, to))
                .thenReturn(Collections.emptyList());

        // When
        List<CheckResult> results = statisticsRetrievalService.getCheckHistory(checkId, from, to, 10, 0);

        // Then
        assertThat(results).isEmpty();
    }

    @Test
    void testGetCheckHistory_WithDefaultDates() {
        // Given
        String checkId = "test-check-1";
        RawCheckResult result = createTestRawCheckResult(checkId, "UP", 100, 200);
        
        when(rawCheckResultRepository.findByKeyCheckIdAndKeyTimestampBetween(eq(checkId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(result));

        // When - null dates should use defaults
        List<CheckResult> results = statisticsRetrievalService.getCheckHistory(checkId, null, null, 10, 0);

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStatus()).isEqualTo("UP");
    }

    @Test
    void testGetAggregatedData_WithResults() {
        // Given
        String checkId = "test-check-1";
        String interval = "HOUR";
        LocalDateTime from = testTimestamp.minusDays(1);
        LocalDateTime to = testTimestamp;
        
        AggregatedCheckResult aggregatedResult = createTestAggregatedCheckResult(checkId, interval);
        
        when(aggregatedCheckResultRepository.findByKeyCheckIdAndKeyAggregationIntervalAndKeyTimestampBetween(
                checkId, interval, from, to))
                .thenReturn(List.of(aggregatedResult));

        // When
        Object result = statisticsRetrievalService.getAggregatedData(checkId, interval, from, to);

        // Then
        assertThat(result).isInstanceOf(List.class);
        List<?> results = (List<?>) result;
        assertThat(results).hasSize(1);
    }

    @Test
    void testGetAggregatedData_WithDefaultDates() {
        // Given
        String checkId = "test-check-1";
        String interval = "HOUR";
        AggregatedCheckResult aggregatedResult = createTestAggregatedCheckResult(checkId, interval);
        
        when(aggregatedCheckResultRepository.findByKeyCheckIdAndKeyAggregationIntervalAndKeyTimestampBetween(
                eq(checkId), eq(interval), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(aggregatedResult));

        // When - null dates should use defaults
        Object result = statisticsRetrievalService.getAggregatedData(checkId, interval, null, null);

        // Then
        assertThat(result).isInstanceOf(List.class);
        List<?> results = (List<?>) result;
        assertThat(results).hasSize(1);
    }

    @Test
    void testGetAggregatedData_NoResults() {
        // Given
        String checkId = "test-check-1";
        String interval = "HOUR";
        LocalDateTime from = testTimestamp.minusDays(1);
        LocalDateTime to = testTimestamp;
        
        when(aggregatedCheckResultRepository.findByKeyCheckIdAndKeyAggregationIntervalAndKeyTimestampBetween(
                checkId, interval, from, to))
                .thenReturn(Collections.emptyList());

        // When
        Object result = statisticsRetrievalService.getAggregatedData(checkId, interval, from, to);

        // Then
        assertThat(result).isInstanceOf(List.class);
        List<?> results = (List<?>) result;
        assertThat(results).isEmpty();
    }

    @Test
    void testGetDashboardData() {
        // When
        Object result = statisticsRetrievalService.getDashboardData();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(StatisticsRetrievalService.DashboardData.class);
    }


    private RawCheckResult createTestRawCheckResult(String checkId, String status, long responseTime, int httpStatusCode) {
        RawCheckResult rawCheckResult = new RawCheckResult();
        
        RawCheckResult.RawCheckResultKey key = new RawCheckResult.RawCheckResultKey();
        key.setCheckId(checkId);
        key.setTimestamp(testTimestamp);
        rawCheckResult.setKey(key);
        
        rawCheckResult.setStatus(status);
        rawCheckResult.setResponseTime((int) responseTime);
        rawCheckResult.setHttpStatusCode(httpStatusCode);
        rawCheckResult.setErrorMessage(null);
        
        return rawCheckResult;
    }

    private AggregatedCheckResult createTestAggregatedCheckResult(String checkId, String interval) {
        AggregatedCheckResult aggregatedResult = new AggregatedCheckResult();
        
        AggregatedCheckResult.AggregatedCheckResultKey key = new AggregatedCheckResult.AggregatedCheckResultKey();
        key.setCheckId(checkId);
        key.setAggregationInterval(interval);
        key.setTimestamp(testTimestamp);
        aggregatedResult.setKey(key);
        
        aggregatedResult.setUpCount(5);
        aggregatedResult.setDownCount(2);
        aggregatedResult.setUnknownCount(0);
        aggregatedResult.setAvgResponseTime(120.5);
        aggregatedResult.setMinResponseTime(50);
        aggregatedResult.setMaxResponseTime(300);
        
        return aggregatedResult;
    }
}