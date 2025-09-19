package ru.oldzoomer.pingtower.settings_manager.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class Role {
    private UUID id;
    private String name;
    private String description;
    private List<String> permissions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}