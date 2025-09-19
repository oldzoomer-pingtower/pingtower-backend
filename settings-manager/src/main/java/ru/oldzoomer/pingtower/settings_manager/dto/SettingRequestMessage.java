package ru.oldzoomer.pingtower.settings_manager.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SettingRequestMessage {
    private UUID requestId;
    private String moduleId;
    private String settingKey;
    private String newValue;
    private String requestedBy;
    private LocalDateTime timestamp;
    private String reason;
}