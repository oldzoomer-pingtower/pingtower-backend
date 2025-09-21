package ru.oldzoomer.pingtower.notificator.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ConfigurationSettingMapper {
    default Object valueToObject(String value, String dataType) {
        if (value == null) return null;
        
        switch (dataType) {
            case "INTEGER":
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    return null;
                }
            case "LONG":
                try {
                    return Long.parseLong(value);
                } catch (NumberFormatException e) {
                    return null;
                }
            case "BOOLEAN":
                return Boolean.parseBoolean(value);
            case "STRING":
            default:
                return value;
        }
    }
    
    default String objectToValue(Object object) {
        return object != null ? object.toString() : null;
    }
    
    default String determineDataType(Object object) {
        if (object instanceof Integer) {
            return "INTEGER";
        } else if (object instanceof Long) {
            return "LONG";
        } else if (object instanceof Boolean) {
            return "BOOLEAN";
        } else {
            return "STRING";
        }
    }
}