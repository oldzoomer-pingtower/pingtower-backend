package ru.oldzoomer.pingtower.pinger.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "check_configurations")
public class CheckConfigurationEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(name = "type")
    private String type;
    
    @Column(name = "resource_url")
    private String resourceUrl;
    
    @Column(name = "frequency")
    private Long frequency;
    
    @Column(name = "timeout")
    private Integer timeout;
    
    @Column(name = "expected_status_code")
    private Integer expectedStatusCode;
    
    @Column(name = "expected_response_time")
    private Long expectedResponseTime;
    
    @Column(name = "validate_ssl")
    private Boolean validateSsl;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}