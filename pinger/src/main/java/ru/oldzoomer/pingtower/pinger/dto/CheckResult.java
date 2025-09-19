package ru.oldzoomer.pingtower.pinger.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CheckResult {
    private String checkId;
    private String resourceUrl;
    private LocalDateTime timestamp;
    private String status;
    private long responseTime;
    private Integer httpStatusCode;
    private String errorMessage;
    private Metrics metrics;

    @Data
    public static class Metrics {
        private long connectionTime;
        private long timeToFirstByte;
        private Boolean sslValid;
        private LocalDateTime sslExpirationDate;
    }
}