package ru.oldzoomer.pingtower.settings_manager.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.oldzoomer.pingtower.settings_manager.dto.Setting;
import ru.oldzoomer.pingtower.settings_manager.entity.SettingEntity;
import ru.oldzoomer.pingtower.settings_manager.exception.EntityNotFoundException;
import ru.oldzoomer.pingtower.settings_manager.repository.SettingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SettingService {

    private final SettingRepository settingRepository;

    public SettingService(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    public List<Setting> getAllSettings() {
        return settingRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "moduleSettings", key = "#module")
    public List<Setting> getSettingsByModule(String module) {
        // Получим из БД
        return settingRepository.findByModule(module).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "setting", key = "#module + ':' + #key")
    public Optional<Setting> getSetting(String module, String key) {
        // Получим из БД
        Optional<SettingEntity> settingEntity = settingRepository.findByModuleAndKey(module, key);
        return settingEntity.map(this::convertToDto);
    }

    @Transactional
    @Caching(
        put = { @CachePut(value = "setting", key = "#result.module + ':' + #result.key") },
        evict = { @CacheEvict(value = "moduleSettings", key = "#result.module") }
    )
    public Setting createSetting(Setting setting) {
        SettingEntity entity = convertToEntity(setting);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setVersion(0);

        SettingEntity savedEntity = settingRepository.save(entity);
        return convertToDto(savedEntity);
    }

    @Transactional
    @Caching(
        put = { @CachePut(value = "setting", key = "#module + ':' + #key") },
        evict = { @CacheEvict(value = "moduleSettings", key = "#module") }
    )
    public Setting updateSetting(String module, String key, Setting setting) {
        Optional<SettingEntity> existingEntity = settingRepository.findByModuleAndKey(module, key);
        if (existingEntity.isPresent()) {
            SettingEntity entity = existingEntity.get();
            entity.setValue(setting.getValue());
            entity.setDescription(setting.getDescription());
            entity.setUpdatedAt(LocalDateTime.now());
            entity.setVersion(entity.getVersion() + 1);

            SettingEntity updatedEntity = settingRepository.save(entity);
            return convertToDto(updatedEntity);
        } else {
            throw new EntityNotFoundException("Setting not found: " + module + "." + key);
        }
    }

    @Transactional
    @Caching(
        evict = {
            @CacheEvict(value = "setting", key = "#module + ':' + #key"),
            @CacheEvict(value = "moduleSettings", key = "#module")
        }
    )
    public void deleteSetting(String module, String key) {
        Optional<SettingEntity> existingEntity = settingRepository.findByModuleAndKey(module, key);
        if (existingEntity.isPresent()) {
            settingRepository.delete(existingEntity.get());
        } else {
            throw new EntityNotFoundException("Setting not found: " + module + "." + key);
        }
    }

    private Setting convertToDto(SettingEntity entity) {
        Setting dto = new Setting();
        dto.setId(entity.getId());
        dto.setModule(entity.getModule());
        dto.setKey(entity.getKey());
        dto.setValue(entity.getValue());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setVersion(entity.getVersion());
        return dto;
    }

    private SettingEntity convertToEntity(Setting dto) {
        SettingEntity entity = new SettingEntity();
        entity.setId(dto.getId());
        entity.setModule(dto.getModule());
        entity.setKey(dto.getKey());
        entity.setValue(dto.getValue());
        entity.setDescription(dto.getDescription());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        entity.setVersion(dto.getVersion());
        return entity;
    }
}