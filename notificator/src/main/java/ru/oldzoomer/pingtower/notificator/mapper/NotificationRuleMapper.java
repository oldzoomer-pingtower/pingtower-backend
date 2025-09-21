package ru.oldzoomer.pingtower.notificator.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import ru.oldzoomer.pingtower.notificator.dto.NotificationRuleDTO;
import ru.oldzoomer.pingtower.notificator.entity.NotificationRuleEntity;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotificationRuleMapper {
    
    ObjectMapper objectMapper = new ObjectMapper();
    
    @Mapping(target = "conditions", source = "conditions", qualifiedByName = "mapConditionsToJson")
    @Mapping(target = "actions", source = "actions", qualifiedByName = "mapActionsToJson")
    NotificationRuleEntity toEntity(NotificationRuleDTO dto);
    
    @Mapping(target = "conditions", source = "conditions", qualifiedByName = "mapJsonToConditions")
    @Mapping(target = "actions", source = "actions", qualifiedByName = "mapJsonToActions")
    NotificationRuleDTO toDto(NotificationRuleEntity entity);
    
    @Named("mapConditionsToJson")
    default String mapConditionsToJson(List<NotificationRuleDTO.Condition> conditions) {
        if (conditions == null) return null;
        try {
            return objectMapper.writeValueAsString(conditions);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
    
    @Named("mapActionsToJson")
    default String mapActionsToJson(List<NotificationRuleDTO.Action> actions) {
        if (actions == null) return null;
        try {
            return objectMapper.writeValueAsString(actions);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
    
    @Named("mapJsonToConditions")
    default List<NotificationRuleDTO.Condition> mapJsonToConditions(String conditions) {
        if (conditions == null || conditions.isEmpty()) return null;
        try {
            return objectMapper.readValue(conditions, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, NotificationRuleDTO.Condition.class));
        } catch (JsonProcessingException e) {
            return null;
        }
    }
    
    @Named("mapJsonToActions")
    default List<NotificationRuleDTO.Action> mapJsonToActions(String actions) {
        if (actions == null || actions.isEmpty()) return null;
        try {
            return objectMapper.readValue(actions, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, NotificationRuleDTO.Action.class));
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}