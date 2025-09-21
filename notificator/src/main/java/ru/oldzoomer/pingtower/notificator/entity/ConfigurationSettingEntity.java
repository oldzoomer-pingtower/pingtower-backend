package ru.oldzoomer.pingtower.notificator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "configuration_settings")
public class ConfigurationSettingEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "setting_key", unique = true, nullable = false)
    private String key;
    
    @Column(name = "setting_value")
    private String value;
    
    @Column(name = "data_type")
    private String dataType;
}