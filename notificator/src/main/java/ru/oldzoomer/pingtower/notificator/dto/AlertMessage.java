package ru.oldzoomer.pingtower.notificator.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AlertMessage {
    private String checkId;
    private String resourceUrl;
    private LocalDateTime timestamp;
    private String status;
    private long downtimeDuration;
    private String errorMessage;
    private String previousStatus;
}