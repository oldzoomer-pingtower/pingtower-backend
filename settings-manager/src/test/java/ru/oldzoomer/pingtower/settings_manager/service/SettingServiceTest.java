package ru.oldzoomer.pingtower.settings_manager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.oldzoomer.pingtower.settings_manager.dto.Setting;
import ru.oldzoomer.pingtower.settings_manager.entity.SettingEntity;
import ru.oldzoomer.pingtower.settings_manager.repository.SettingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettingServiceTest {

    @Mock
    private SettingRepository settingRepository;

    @InjectMocks
    private SettingService settingService;

    private Setting testSetting;
    private SettingEntity testEntity;

    @BeforeEach
    void setUp() {
        UUID testId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        testSetting = new Setting();
        testSetting.setId(testId);
        testSetting.setModule("pinger");
        testSetting.setKey("timeout");
        testSetting.setValue("5000");
        testSetting.setDescription("Таймаут проверки в миллисекундах");
        testSetting.setCreatedAt(now);
        testSetting.setUpdatedAt(now);
        testSetting.setVersion(1);

        testEntity = new SettingEntity();
        testEntity.setId(testId);
        testEntity.setModule("pinger");
        testEntity.setKey("timeout");
        testEntity.setValue("5000");
        testEntity.setDescription("Таймаут проверки в миллисекундах");
        testEntity.setCreatedAt(now);
        testEntity.setUpdatedAt(now);
        testEntity.setVersion(1);
    }

    @Test
    void testGetAllSettings() {
        when(settingRepository.findAll()).thenReturn(List.of(testEntity));

        List<Setting> result = settingService.getAllSettings();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("pinger", result.get(0).getModule());
        assertEquals("timeout", result.get(0).getKey());
        verify(settingRepository).findAll();
    }

    @Test
    void testGetAllSettings_Empty() {
        when(settingRepository.findAll()).thenReturn(List.of());

        List<Setting> result = settingService.getAllSettings();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(settingRepository).findAll();
    }

    @Test
    void testGetSettingsByModule() {
        when(settingRepository.findByModule("pinger")).thenReturn(List.of(testEntity));

        List<Setting> result = settingService.getSettingsByModule("pinger");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("pinger", result.get(0).getModule());
        verify(settingRepository).findByModule("pinger");
    }

    @Test
    void testGetSettingsByModule_Empty() {
        when(settingRepository.findByModule("nonexistent")).thenReturn(List.of());

        List<Setting> result = settingService.getSettingsByModule("nonexistent");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(settingRepository).findByModule("nonexistent");
    }

    @Test
    void testGetSetting_Exists() {
        when(settingRepository.findByModuleAndKey("pinger", "timeout"))
                .thenReturn(Optional.of(testEntity));

        Optional<Setting> result = settingService.getSetting("pinger", "timeout");

        assertTrue(result.isPresent());
        assertEquals("pinger", result.get().getModule());
        assertEquals("timeout", result.get().getKey());
        assertEquals("5000", result.get().getValue());
        verify(settingRepository).findByModuleAndKey("pinger", "timeout");
    }

    @Test
    void testGetSetting_NotExists() {
        when(settingRepository.findByModuleAndKey("pinger", "nonexistent"))
                .thenReturn(Optional.empty());

        Optional<Setting> result = settingService.getSetting("pinger", "nonexistent");

        assertFalse(result.isPresent());
        verify(settingRepository).findByModuleAndKey("pinger", "nonexistent");
    }

    @Test
    void testCreateSetting() {
        when(settingRepository.save(any(SettingEntity.class))).thenReturn(testEntity);

        Setting result = settingService.createSetting(testSetting);

        assertNotNull(result);
        assertEquals("pinger", result.getModule());
        assertEquals("timeout", result.getKey());
        assertEquals("5000", result.getValue());
        verify(settingRepository).save(any(SettingEntity.class));
    }

    @Test
    void testUpdateSetting_Exists() {
        Setting updatedSetting = new Setting();
        updatedSetting.setValue("10000");
        updatedSetting.setDescription("Обновленный таймаут");

        when(settingRepository.findByModuleAndKey("pinger", "timeout"))
                .thenReturn(Optional.of(testEntity));
        when(settingRepository.save(any(SettingEntity.class))).thenReturn(testEntity);

        Setting result = settingService.updateSetting("pinger", "timeout", updatedSetting);

        assertNotNull(result);
        assertEquals("pinger", result.getModule());
        assertEquals("timeout", result.getKey());
        verify(settingRepository).findByModuleAndKey("pinger", "timeout");
        verify(settingRepository).save(any(SettingEntity.class));
    }

    @Test
    void testUpdateSetting_NotExists() {
        Setting updatedSetting = new Setting();
        updatedSetting.setValue("10000");

        when(settingRepository.findByModuleAndKey("pinger", "nonexistent"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            settingService.updateSetting("pinger", "nonexistent", updatedSetting);
        });

        verify(settingRepository).findByModuleAndKey("pinger", "nonexistent");
        verify(settingRepository, never()).save(any(SettingEntity.class));
    }

    @Test
    void testDeleteSetting_Exists() {
        when(settingRepository.findByModuleAndKey("pinger", "timeout"))
                .thenReturn(Optional.of(testEntity));

        assertDoesNotThrow(() -> {
            settingService.deleteSetting("pinger", "timeout");
        });

        verify(settingRepository).findByModuleAndKey("pinger", "timeout");
        verify(settingRepository).delete(testEntity);
    }

    @Test
    void testDeleteSetting_NotExists() {
        when(settingRepository.findByModuleAndKey("pinger", "nonexistent"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            settingService.deleteSetting("pinger", "nonexistent");
        });

        verify(settingRepository).findByModuleAndKey("pinger", "nonexistent");
        verify(settingRepository, never()).delete(any(SettingEntity.class));
    }


    @Test
    void testCreateSetting_NullValues() {
        Setting settingWithNulls = new Setting();
        settingWithNulls.setModule("test");
        settingWithNulls.setKey("test");

        when(settingRepository.save(any(SettingEntity.class))).thenReturn(testEntity);

        Setting result = settingService.createSetting(settingWithNulls);

        assertNotNull(result);
        verify(settingRepository).save(any(SettingEntity.class));
    }

    @Test
    void testUpdateSetting_PartialUpdate() {
        Setting partialUpdate = new Setting();
        partialUpdate.setValue("new-value");

        when(settingRepository.findByModuleAndKey("pinger", "timeout"))
                .thenReturn(Optional.of(testEntity));
        when(settingRepository.save(any(SettingEntity.class))).thenReturn(testEntity);

        Setting result = settingService.updateSetting("pinger", "timeout", partialUpdate);

        assertNotNull(result);
        verify(settingRepository).findByModuleAndKey("pinger", "timeout");
        verify(settingRepository).save(any(SettingEntity.class));
    }
}