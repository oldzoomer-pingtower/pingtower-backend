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
@Table(name = "notification_rules")
public class NotificationRuleEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "conditions", columnDefinition = "jsonb")
    private String conditions;
    
    @Column(name = "actions", columnDefinition = "jsonb")
    private String actions;
    
    @Column(name = "enabled")
    private boolean enabled;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}