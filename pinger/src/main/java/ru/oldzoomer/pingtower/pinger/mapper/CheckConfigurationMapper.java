package ru.oldzoomer.pingtower.pinger.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.oldzoomer.pingtower.pinger.dto.CheckConfigurationDTO;
import ru.oldzoomer.pingtower.pinger.entity.CheckConfigurationEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CheckConfigurationMapper {
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "resourceUrl", source = "resourceUrl")
    @Mapping(target = "frequency", source = "frequency")
    @Mapping(target = "timeout", source = "timeout")
    @Mapping(target = "expectedStatusCode", source = "expectedStatusCode")
    @Mapping(target = "expectedResponseTime", source = "expectedResponseTime")
    @Mapping(target = "validateSsl", source = "validateSsl")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    CheckConfigurationDTO toDto(CheckConfigurationEntity entity);
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "resourceUrl", source = "resourceUrl")
    @Mapping(target = "frequency", source = "frequency")
    @Mapping(target = "timeout", source = "timeout")
    @Mapping(target = "expectedStatusCode", source = "expectedStatusCode")
    @Mapping(target = "expectedResponseTime", source = "expectedResponseTime")
    @Mapping(target = "validateSsl", source = "validateSsl")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    CheckConfigurationEntity toEntity(CheckConfigurationDTO dto);
}