package ru.oldzoomer.pingtower.settings_manager.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class User {
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}