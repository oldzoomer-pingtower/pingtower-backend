package ru.oldzoomer.pingtower.statistics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "Результат проверки")
public class CheckResult {
    @Schema(description = "Идентификатор проверки", example = "check-12345")
    private String checkId;
    
    @Schema(description = "URL ресурса", example = "https://example.com")
    private String resourceUrl;
    
    @Schema(description = "Время выполнения проверки", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;
    
    @Schema(description = "Статус проверки", example = "SUCCESS", allowableValues = {"SUCCESS", "FAILURE", "TIMEOUT"})
    private String status;
    
    @Schema(description = "Время ответа в миллисекундах", example = "150")
    private long responseTime;
    
    @Schema(description = "HTTP статус код", example = "200")
    private Integer httpStatusCode;
    
    @Schema(description = "Сообщение об ошибке", example = "Connection timeout")
    private String errorMessage;
    
    @Schema(description = "Метрики проверки")
    private Metrics metrics;

    @Data
    @Schema(description = "Метрики проверки")
    public static class Metrics {
        @Schema(description = "Время установки соединения в миллисекундах", example = "50")
        private long connectionTime;
        
        @Schema(description = "Время до первого байта в миллисекундах", example = "100")
        private long timeToFirstByte;
        
        @Schema(description = "Валидность SSL сертификата", example = "true")
        private Boolean sslValid;
        
        @Schema(description = "Дата истечения SSL сертификата", example = "2024-12-31T23:59:59")
        private LocalDateTime sslExpirationDate;
    }
}