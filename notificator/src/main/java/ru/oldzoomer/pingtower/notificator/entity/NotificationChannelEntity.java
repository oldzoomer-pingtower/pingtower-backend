package ru.oldzoomer.pingtower.notificator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notification_channels")
public class NotificationChannelEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(name = "type")
    private String type;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "configuration", columnDefinition = "jsonb")
    private String configuration;
    
    @Column(name = "enabled")
    private boolean enabled;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}