package ru.oldzoomer.pingtower.statistics.cassandra.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.oldzoomer.pingtower.statistics.TestConfiguration;
import ru.oldzoomer.pingtower.statistics.cassandra.entity.AggregatedCheckResult;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AggregatedCheckResultRepositoryTest extends TestConfiguration {

    @Autowired
    private AggregatedCheckResultRepository aggregatedCheckResultRepository;

    private LocalDateTime testTimestamp1;
    private LocalDateTime testTimestamp2;
    private LocalDateTime testTimestamp3;

    @BeforeEach
    void setUp() {
        aggregatedCheckResultRepository.deleteAll();
        
        testTimestamp1 = LocalDateTime.of(2024, 1, 15, 10, 0, 0);
        testTimestamp2 = LocalDateTime.of(2024, 1, 15, 11, 0, 0);
        testTimestamp3 = LocalDateTime.of(2024, 1, 16, 10, 0, 0);
    }

    @Test
    void testSaveAndFindById() {
        // Given
        AggregatedCheckResult result = createTestAggregatedCheckResult("test-check-1", "HOURLY", testTimestamp1, 10, 2, 1);

        // When
        AggregatedCheckResult saved = aggregatedCheckResultRepository.save(result);
        var found = aggregatedCheckResultRepository.findById(saved.getKey());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getKey().getCheckId()).isEqualTo("test-check-1");
        assertThat(found.get().getKey().getAggregationInterval()).isEqualTo("HOURLY");
        assertThat(found.get().getUpCount()).isEqualTo(10);
        assertThat(found.get().getDownCount()).isEqualTo(2);
        assertThat(found.get().getUnknownCount()).isEqualTo(1);
    }

    @Test
    void testFindByKeyCheckIdAndKeyAggregationIntervalAndKeyTimestampBetween() {
        // Given
        AggregatedCheckResult result1 = createTestAggregatedCheckResult("test-check-1", "HOURLY", testTimestamp1, 10, 2, 1);
        AggregatedCheckResult result2 = createTestAggregatedCheckResult("test-check-1", "HOURLY", testTimestamp2, 15, 3, 0);
        AggregatedCheckResult result3 = createTestAggregatedCheckResult("test-check-1", "DAILY", testTimestamp3, 100, 5, 2);
        AggregatedCheckResult result4 = createTestAggregatedCheckResult("test-check-2", "HOURLY", testTimestamp1, 8, 1, 0);

        aggregatedCheckResultRepository.saveAll(List.of(result1, result2, result3, result4));

        // When
        LocalDateTime from = LocalDateTime.of(2024, 1, 15, 9, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 1, 15, 12, 0, 0);
        List<AggregatedCheckResult> results = aggregatedCheckResultRepository.findByKeyCheckIdAndKeyAggregationIntervalAndKeyTimestampBetween(
                "test-check-1", "HOURLY", from, to);

        // Then
        assertThat(results).hasSize(2);
        assertThat(results).extracting(r -> r.getKey().getTimestamp())
                .containsExactlyInAnyOrder(testTimestamp1, testTimestamp2);
        assertThat(results).extracting(r -> r.getUpCount())
                .containsExactlyInAnyOrder(10, 15);
        assertThat(results).extracting(r -> r.getDownCount())
                .containsExactlyInAnyOrder(2, 3);
    }

    @Test
    void testFindByKeyCheckIdAndKeyAggregationIntervalAndKeyTimestampBetween_DifferentInterval() {
        // Given
        AggregatedCheckResult result1 = createTestAggregatedCheckResult("test-check-1", "HOURLY", testTimestamp1, 10, 2, 1);
        AggregatedCheckResult result2 = createTestAggregatedCheckResult("test-check-1", "DAILY", testTimestamp3, 100, 5, 2);

        aggregatedCheckResultRepository.saveAll(List.of(result1, result2));

        // When
        LocalDateTime from = LocalDateTime.of(2024, 1, 15, 0, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 1, 17, 0, 0, 0);
        List<AggregatedCheckResult> results = aggregatedCheckResultRepository.findByKeyCheckIdAndKeyAggregationIntervalAndKeyTimestampBetween(
                "test-check-1", "DAILY", from, to);

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getKey().getAggregationInterval()).isEqualTo("DAILY");
        assertThat(results.get(0).getUpCount()).isEqualTo(100);
        assertThat(results.get(0).getDownCount()).isEqualTo(5);
    }

    @Test
    void testFindByKeyCheckIdAndKeyAggregationIntervalAndKeyTimestampBetween_NoResults() {
        // Given
        AggregatedCheckResult result = createTestAggregatedCheckResult("test-check-1", "HOURLY", testTimestamp1, 10, 2, 1);
        aggregatedCheckResultRepository.save(result);

        // When
        LocalDateTime from = LocalDateTime.of(2024, 1, 16, 10, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 1, 16, 11, 0, 0);
        List<AggregatedCheckResult> results = aggregatedCheckResultRepository.findByKeyCheckIdAndKeyAggregationIntervalAndKeyTimestampBetween(
                "test-check-1", "HOURLY", from, to);

        // Then
        assertThat(results).isEmpty();
    }

    @Test
    void testFindByKeyCheckIdAndKeyAggregationIntervalAndKeyTimestampBetween_WrongCheckId() {
        // Given
        AggregatedCheckResult result = createTestAggregatedCheckResult("test-check-1", "HOURLY", testTimestamp1, 10, 2, 1);
        aggregatedCheckResultRepository.save(result);

        // When
        LocalDateTime from = LocalDateTime.of(2024, 1, 15, 9, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 1, 15, 12, 0, 0);
        List<AggregatedCheckResult> results = aggregatedCheckResultRepository.findByKeyCheckIdAndKeyAggregationIntervalAndKeyTimestampBetween(
                "wrong-check-id", "HOURLY", from, to);

        // Then
        assertThat(results).isEmpty();
    }

    @Test
    void testFindByKeyCheckIdAndKeyAggregationIntervalAndKeyTimestampBetween_WrongInterval() {
        // Given
        AggregatedCheckResult result = createTestAggregatedCheckResult("test-check-1", "HOURLY", testTimestamp1, 10, 2, 1);
        aggregatedCheckResultRepository.save(result);

        // When
        LocalDateTime from = LocalDateTime.of(2024, 1, 15, 9, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 1, 15, 12, 0, 0);
        List<AggregatedCheckResult> results = aggregatedCheckResultRepository.findByKeyCheckIdAndKeyAggregationIntervalAndKeyTimestampBetween(
                "test-check-1", "DAILY", from, to);

        // Then
        assertThat(results).isEmpty();
    }

    @Test
    void testDeleteAll() {
        // Given
        AggregatedCheckResult result1 = createTestAggregatedCheckResult("test-check-1", "HOURLY", testTimestamp1, 10, 2, 1);
        AggregatedCheckResult result2 = createTestAggregatedCheckResult("test-check-2", "DAILY", testTimestamp3, 100, 5, 2);
        aggregatedCheckResultRepository.saveAll(List.of(result1, result2));

        // When
        aggregatedCheckResultRepository.deleteAll();
        long count = aggregatedCheckResultRepository.count();

        // Then
        assertThat(count).isZero();
    }

    @Test
    void testCount() {
        // Given
        AggregatedCheckResult result1 = createTestAggregatedCheckResult("test-check-1", "HOURLY", testTimestamp1, 10, 2, 1);
        AggregatedCheckResult result2 = createTestAggregatedCheckResult("test-check-2", "DAILY", testTimestamp3, 100, 5, 2);
        aggregatedCheckResultRepository.saveAll(List.of(result1, result2));

        // When
        long count = aggregatedCheckResultRepository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }

    private AggregatedCheckResult createTestAggregatedCheckResult(String checkId, String interval, 
                                                                 LocalDateTime timestamp, int upCount, 
                                                                 int downCount, int unknownCount) {
        AggregatedCheckResult result = new AggregatedCheckResult();
        AggregatedCheckResult.AggregatedCheckResultKey key = new AggregatedCheckResult.AggregatedCheckResultKey();
        key.setCheckId(checkId);
        key.setAggregationInterval(interval);
        key.setTimestamp(timestamp);
        result.setKey(key);
        result.setUpCount(upCount);
        result.setDownCount(downCount);
        result.setUnknownCount(unknownCount);
        result.setAvgResponseTime(150.5);
        result.setMinResponseTime(50);
        result.setMaxResponseTime(500);
        return result;
    }
}