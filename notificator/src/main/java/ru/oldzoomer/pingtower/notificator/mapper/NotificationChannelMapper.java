package ru.oldzoomer.pingtower.notificator.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import ru.oldzoomer.pingtower.notificator.dto.NotificationChannelDTO;
import ru.oldzoomer.pingtower.notificator.entity.NotificationChannelEntity;

import java.util.Map;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotificationChannelMapper {
    
    ObjectMapper objectMapper = new ObjectMapper();
    
    @Mapping(target = "configuration", source = "configuration", qualifiedByName = "mapConfigToJson")
    NotificationChannelEntity toEntity(NotificationChannelDTO dto);
    
    @Mapping(target = "configuration", source = "configuration", qualifiedByName = "mapJsonToConfig")
    NotificationChannelDTO toDto(NotificationChannelEntity entity);
    
    @Named("mapConfigToJson")
    default String mapConfigToJson(Map<String, Object> config) {
        if (config == null) return null;
        try {
            return objectMapper.writeValueAsString(config);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
    
    @Named("mapJsonToConfig")
    default Map<String, Object> mapJsonToConfig(String config) {
        if (config == null || config.isEmpty()) return null;
        try {
            return objectMapper.readValue(config, Map.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}