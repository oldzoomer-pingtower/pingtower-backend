package ru.oldzoomer.pingtower.settings_manager.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SettingUpdateMessage {
    private UUID settingId;
    private String module;
    private String key;
    private String value;
    private LocalDateTime timestamp;
    private Integer version;
    private String action; // CREATE, UPDATE, DELETE
}