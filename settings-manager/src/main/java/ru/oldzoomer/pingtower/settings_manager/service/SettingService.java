package ru.oldzoomer.pingtower.settings_manager.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.oldzoomer.pingtower.settings_manager.dto.Setting;
import ru.oldzoomer.pingtower.settings_manager.entity.SettingEntity;
import ru.oldzoomer.pingtower.settings_manager.repository.SettingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SettingService {

    private final SettingRepository settingRepository;
    private final CacheService cacheService;

    public SettingService(SettingRepository settingRepository, CacheService cacheService) {
        this.settingRepository = settingRepository;
        this.cacheService = cacheService;
    }

    public List<Setting> getAllSettings() {
        return settingRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<Setting> getSettingsByModule(String module) {
        // Попробуем сначала получить из кэша
        List<Setting> cachedSettings = cacheService.getModuleSettingsFromCache(module);
        if (cachedSettings != null) {
            return cachedSettings;
        }

        // Если нет в кэше, получим из БД
        List<Setting> settings = settingRepository.findByModule(module).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        // Сохраним в кэш
        cacheService.cacheModuleSettings(module, settings);

        return settings;
    }

    public Optional<Setting> getSetting(String module, String key) {
        // Попробуем сначала получить из кэша
        Setting cachedSetting = cacheService.getSettingFromCache(module, key);
        if (cachedSetting != null) {
            return Optional.of(cachedSetting);
        }

        // Если нет в кэше, получим из БД
        Optional<SettingEntity> settingEntity = settingRepository.findByModuleAndKey(module, key);
        if (settingEntity.isPresent()) {
            Setting setting = convertToDto(settingEntity.get());
            // Сохраним в кэш
            cacheService.cacheSetting(setting);
            return Optional.of(setting);
        }

        return Optional.empty();
    }

    @Transactional
    public Setting createSetting(Setting setting) {
        SettingEntity entity = convertToEntity(setting);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setVersion(0);

        SettingEntity savedEntity = settingRepository.save(entity);
        Setting savedSetting = convertToDto(savedEntity);

        // Обновим кэш
        cacheService.cacheSetting(savedSetting);
        cacheService.invalidateModuleCache(savedSetting.getModule());

        return savedSetting;
    }

    @Transactional
    public Setting updateSetting(String module, String key, Setting setting) {
        Optional<SettingEntity> existingEntity = settingRepository.findByModuleAndKey(module, key);
        if (existingEntity.isPresent()) {
            SettingEntity entity = existingEntity.get();
            entity.setValue(setting.getValue());
            entity.setDescription(setting.getDescription());
            entity.setUpdatedAt(LocalDateTime.now());
            entity.setVersion(entity.getVersion() + 1);

            SettingEntity updatedEntity = settingRepository.save(entity);
            Setting updatedSetting = convertToDto(updatedEntity);

            // Обновим кэш
            cacheService.cacheSetting(updatedSetting);
            cacheService.invalidateModuleCache(updatedSetting.getModule());

            return updatedSetting;
        } else {
            throw new RuntimeException("Setting not found: " + module + "." + key);
        }
    }

    @Transactional
    public void deleteSetting(String module, String key) {
        Optional<SettingEntity> existingEntity = settingRepository.findByModuleAndKey(module, key);
        if (existingEntity.isPresent()) {
            settingRepository.delete(existingEntity.get());

            // Удалим из кэша
            cacheService.invalidateSettingCache(module, key);
            cacheService.invalidateModuleCache(module);
        } else {
            throw new RuntimeException("Setting not found: " + module + "." + key);
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