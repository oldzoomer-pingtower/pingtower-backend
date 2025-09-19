package ru.oldzoomer.pingtower.settings_manager.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Setting {
    private UUID id;
    private String module;
    private String key;
    private String value;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer version;
}