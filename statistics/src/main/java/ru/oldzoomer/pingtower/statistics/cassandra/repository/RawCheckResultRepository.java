package ru.oldzoomer.pingtower.statistics.cassandra.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import ru.oldzoomer.pingtower.statistics.cassandra.entity.RawCheckResult;
import ru.oldzoomer.pingtower.statistics.cassandra.entity.RawCheckResult.RawCheckResultKey;

@Repository
public interface RawCheckResultRepository extends CassandraRepository<RawCheckResult, RawCheckResultKey> {
    List<RawCheckResult> findByKeyCheckIdAndKeyTimestampBetween(String checkId, LocalDateTime from, LocalDateTime to);
    List<RawCheckResult> findByKeyCheckIdOrderByKeyTimestampDesc(String checkId);
}