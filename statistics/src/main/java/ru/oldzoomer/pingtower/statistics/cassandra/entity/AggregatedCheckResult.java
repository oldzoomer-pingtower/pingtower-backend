package ru.oldzoomer.pingtower.statistics.cassandra.entity;

import java.time.LocalDateTime;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.Data;

@Data
@Table("aggregated_check_results")
public class AggregatedCheckResult {
    @PrimaryKey
    private AggregatedCheckResultKey key;
    
    private Integer upCount;
    private Integer downCount;
    private Integer unknownCount;
    private Double avgResponseTime;
    private Integer minResponseTime;
    private Integer maxResponseTime;
    
    @Data
    public static class AggregatedCheckResultKey {
        private String checkId;
        private String aggregationInterval; // MINUTE, HOUR, DAY, WEEK, MONTH
        private LocalDateTime timestamp;
    }
}