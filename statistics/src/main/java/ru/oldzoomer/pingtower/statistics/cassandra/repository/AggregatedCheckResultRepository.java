package ru.oldzoomer.pingtower.statistics.cassandra.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import ru.oldzoomer.pingtower.statistics.cassandra.entity.AggregatedCheckResult;
import ru.oldzoomer.pingtower.statistics.cassandra.entity.AggregatedCheckResult.AggregatedCheckResultKey;

@Repository
public interface AggregatedCheckResultRepository extends CassandraRepository<AggregatedCheckResult, AggregatedCheckResultKey> {
    List<AggregatedCheckResult> findByKeyCheckIdAndKeyAggregationIntervalAndKeyTimestampBetween(
        String checkId, String aggregationInterval, LocalDateTime from, LocalDateTime to);
}