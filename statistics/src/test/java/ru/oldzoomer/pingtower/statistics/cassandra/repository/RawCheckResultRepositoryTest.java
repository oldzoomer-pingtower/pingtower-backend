package ru.oldzoomer.pingtower.statistics.cassandra.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.oldzoomer.pingtower.statistics.TestConfiguration;
import ru.oldzoomer.pingtower.statistics.cassandra.entity.RawCheckResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RawCheckResultRepositoryTest extends TestConfiguration {

    @Autowired
    private RawCheckResultRepository rawCheckResultRepository;

    private LocalDateTime testTimestamp1;
    private LocalDateTime testTimestamp2;
    private LocalDateTime testTimestamp3;

    @BeforeEach
    void setUp() {
        rawCheckResultRepository.deleteAll();
        
        testTimestamp1 = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
        testTimestamp2 = LocalDateTime.of(2024, 1, 15, 10, 31, 0);
        testTimestamp3 = LocalDateTime.of(2024, 1, 15, 11, 30, 0);
    }

    @Test
    void testSaveAndFindById() {
        // Given
        RawCheckResult result = createTestRawCheckResult("test-check-1", testTimestamp1, "UP", 150, 200);

        // When
        RawCheckResult saved = rawCheckResultRepository.save(result);
        var found = rawCheckResultRepository.findById(saved.getKey());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getKey().getCheckId()).isEqualTo("test-check-1");
        assertThat(found.get().getStatus()).isEqualTo("UP");
        assertThat(found.get().getResponseTime()).isEqualTo(150);
    }

    @Test
    void testFindByKeyCheckIdAndKeyTimestampBetween() {
        // Given
        RawCheckResult result1 = createTestRawCheckResult("test-check-1", testTimestamp1, "UP", 150, 200);
        RawCheckResult result2 = createTestRawCheckResult("test-check-1", testTimestamp2, "DOWN", 500, 500);
        RawCheckResult result3 = createTestRawCheckResult("test-check-2", testTimestamp3, "UP", 200, 200);

        rawCheckResultRepository.saveAll(List.of(result1, result2, result3));

        // When
        LocalDateTime from = LocalDateTime.of(2024, 1, 15, 10, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 1, 15, 11, 0, 0);
        List<RawCheckResult> results = rawCheckResultRepository.findByKeyCheckIdAndKeyTimestampBetween(
                "test-check-1", from, to);

        // Then
        assertThat(results).hasSize(2);
        assertThat(results).extracting(r -> r.getKey().getTimestamp())
                .containsExactlyInAnyOrder(testTimestamp1, testTimestamp2);
        assertThat(results).extracting(r -> r.getStatus())
                .containsExactlyInAnyOrder("UP", "DOWN");
    }

    @Test
    void testFindByKeyCheckIdOrderByKeyTimestampDesc() {
        // Given
        RawCheckResult result1 = createTestRawCheckResult("test-check-1", testTimestamp1, "UP", 150, 200);
        RawCheckResult result2 = createTestRawCheckResult("test-check-1", testTimestamp2, "DOWN", 500, 500);
        RawCheckResult result3 = createTestRawCheckResult("test-check-1", testTimestamp3, "UP", 200, 200);

        rawCheckResultRepository.saveAll(List.of(result1, result2, result3));

        // When
        List<RawCheckResult> results = rawCheckResultRepository.findByKeyCheckIdOrderByKeyTimestampDesc("test-check-1");

        // Then
        assertThat(results).hasSize(3);
        assertThat(results).extracting(r -> r.getKey().getTimestamp())
                .containsExactly(testTimestamp3, testTimestamp2, testTimestamp1);
        assertThat(results.get(0).getStatus()).isEqualTo("UP");
        assertThat(results.get(1).getStatus()).isEqualTo("DOWN");
        assertThat(results.get(2).getStatus()).isEqualTo("UP");
    }

    @Test
    void testFindByKeyCheckIdAndKeyTimestampBetween_NoResults() {
        // Given
        RawCheckResult result = createTestRawCheckResult("test-check-1", testTimestamp1, "UP", 150, 200);
        rawCheckResultRepository.save(result);

        // When
        LocalDateTime from = LocalDateTime.of(2024, 1, 16, 10, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 1, 16, 11, 0, 0);
        List<RawCheckResult> results = rawCheckResultRepository.findByKeyCheckIdAndKeyTimestampBetween(
                "test-check-1", from, to);

        // Then
        assertThat(results).isEmpty();
    }

    @Test
    void testFindByKeyCheckIdOrderByKeyTimestampDesc_NoResults() {
        // Given
        // No data for this check ID

        // When
        List<RawCheckResult> results = rawCheckResultRepository.findByKeyCheckIdOrderByKeyTimestampDesc("non-existent-check");

        // Then
        assertThat(results).isEmpty();
    }

    @Test
    void testDeleteAll() {
        // Given
        RawCheckResult result1 = createTestRawCheckResult("test-check-1", testTimestamp1, "UP", 150, 200);
        RawCheckResult result2 = createTestRawCheckResult("test-check-2", testTimestamp2, "DOWN", 500, 500);
        rawCheckResultRepository.saveAll(List.of(result1, result2));

        // When
        rawCheckResultRepository.deleteAll();
        long count = rawCheckResultRepository.count();

        // Then
        assertThat(count).isZero();
    }

    @Test
    void testCount() {
        // Given
        RawCheckResult result1 = createTestRawCheckResult("test-check-1", testTimestamp1, "UP", 150, 200);
        RawCheckResult result2 = createTestRawCheckResult("test-check-2", testTimestamp2, "DOWN", 500, 500);
        rawCheckResultRepository.saveAll(List.of(result1, result2));

        // When
        long count = rawCheckResultRepository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }

    private RawCheckResult createTestRawCheckResult(String checkId, LocalDateTime timestamp, 
                                                   String status, int responseTime, int httpStatusCode) {
        RawCheckResult result = new RawCheckResult();
        RawCheckResult.RawCheckResultKey key = new RawCheckResult.RawCheckResultKey();
        key.setCheckId(checkId);
        key.setTimestamp(timestamp);
        result.setKey(key);
        result.setStatus(status);
        result.setResponseTime(responseTime);
        result.setHttpStatusCode(httpStatusCode);
        result.setErrorMessage(null);
        result.setMetrics(Map.of("connectionTime", "50", "timeToFirstByte", "100"));
        return result;
    }
}