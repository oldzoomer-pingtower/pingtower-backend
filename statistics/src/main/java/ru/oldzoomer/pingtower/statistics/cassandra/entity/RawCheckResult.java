package ru.oldzoomer.pingtower.statistics.cassandra.entity;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.Data;

@Data
@Table("raw_check_results")
public class RawCheckResult {
    @PrimaryKey
    private RawCheckResultKey key;
    
    private String status;
    private Integer responseTime;
    private Integer httpStatusCode;
    private String errorMessage;
    private Map<String, String> metrics;
    
    @Data
    public static class RawCheckResultKey {
        private String checkId;
        private LocalDateTime timestamp;
    }
}