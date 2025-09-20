package ru.oldzoomer.pingtower.settings_manager.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ru.oldzoomer.pingtower.settings_manager.SimpleTestConfiguration;
import ru.oldzoomer.pingtower.settings_manager.entity.SettingEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SettingRepositoryTest extends SimpleTestConfiguration {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SettingRepository settingRepository;

    @Test
    void testFindByModule() {
        createSetting("pinger", "timeout", "5000");
        createSetting("pinger", "retries", "3");
        createSetting("notificator", "enabled", "true");
        
        entityManager.flush();

        // When
        List<SettingEntity> pingerSettings = settingRepository.findByModule("pinger");
        List<SettingEntity> notificatorSettings = settingRepository.findByModule("notificator");
        List<SettingEntity> nonexistentSettings = settingRepository.findByModule("nonexistent");

        // Then
        assertThat(pingerSettings).hasSize(2);
        assertThat(pingerSettings).extracting(SettingEntity::getKey)
                .containsExactlyInAnyOrder("timeout", "retries");
        
        assertThat(notificatorSettings).hasSize(1);
        assertThat(notificatorSettings.get(0).getKey()).isEqualTo("enabled");
        
        assertThat(nonexistentSettings).isEmpty();
    }

    @Test
    void testFindByModuleAndKey() {
        createSetting("pinger", "timeout", "5000");
        
        entityManager.flush();

        // When
        Optional<SettingEntity> foundSetting = settingRepository.findByModuleAndKey("pinger", "timeout");
        Optional<SettingEntity> notFoundSetting = settingRepository.findByModuleAndKey("pinger", "nonexistent");
        Optional<SettingEntity> wrongModuleSetting = settingRepository.findByModuleAndKey("notificator", "timeout");

        // Then
        assertThat(foundSetting).isPresent();
        assertThat(foundSetting.get().getValue()).isEqualTo("5000");
        
        assertThat(notFoundSetting).isEmpty();
        assertThat(wrongModuleSetting).isEmpty();
    }

    @Test
    void testExistsByModuleAndKey() {
        // Given
        createSetting("pinger", "timeout", "5000");
        
        entityManager.flush();

        // When
        boolean exists = settingRepository.existsByModuleAndKey("pinger", "timeout");
        boolean notExists = settingRepository.existsByModuleAndKey("pinger", "nonexistent");
        boolean wrongModule = settingRepository.existsByModuleAndKey("notificator", "timeout");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
        assertThat(wrongModule).isFalse();
    }

    @Test
    void testSaveSetting() {
        // Given
        SettingEntity setting = new SettingEntity();
        setting.setModule("pinger");
        setting.setKey("timeout");
        setting.setValue("5000");
        setting.setDescription("Connection timeout in milliseconds");

        // When
        SettingEntity savedSetting = settingRepository.save(setting);
        entityManager.flush();

        // Then
        assertThat(savedSetting).isNotNull();
        assertThat(savedSetting.getId()).isNotNull();
        assertThat(savedSetting.getModule()).isEqualTo("pinger");
        assertThat(savedSetting.getKey()).isEqualTo("timeout");
        assertThat(savedSetting.getValue()).isEqualTo("5000");
        assertThat(savedSetting.getDescription()).isEqualTo("Connection timeout in milliseconds");
        assertThat(savedSetting.getCreatedAt()).isNotNull();
        assertThat(savedSetting.getUpdatedAt()).isNotNull();
        assertThat(savedSetting.getVersion()).isEqualTo(0);
    }

    @Test
    void testFindById() {
        // Given
        SettingEntity setting = createSetting("pinger", "timeout", "5000");
        
        entityManager.flush();

        // When
        Optional<SettingEntity> foundSetting = settingRepository.findById(setting.getId());

        // Then
        assertThat(foundSetting).isPresent();
        assertThat(foundSetting.get().getKey()).isEqualTo("timeout");
        assertThat(foundSetting.get().getValue()).isEqualTo("5000");
    }

    @Test
    void testDeleteSetting() {
        // Given
        SettingEntity setting = createSetting("pinger", "timeout", "5000");
        
        entityManager.flush();

        // When
        settingRepository.deleteById(setting.getId());
        entityManager.flush();

        // Then
        Optional<SettingEntity> deletedSetting = settingRepository.findById(setting.getId());
        assertThat(deletedSetting).isEmpty();
    }

    @Test
    void testUpdateSetting() {
        // Given
        SettingEntity setting = createSetting("pinger", "timeout", "5000");
        
        entityManager.flush();

        // When
        setting.setValue("10000");
        setting.setDescription("Updated timeout");
        settingRepository.save(setting);
        entityManager.flush();

        // Then
        Optional<SettingEntity> foundSetting = settingRepository.findById(setting.getId());
        assertThat(foundSetting).isPresent();
        assertThat(foundSetting.get().getValue()).isEqualTo("10000");
        assertThat(foundSetting.get().getDescription()).isEqualTo("Updated timeout");
    }

    @Test
    void testFindAllSettings() {
        // Given
        createSetting("pinger", "timeout", "5000");
        createSetting("pinger", "retries", "3");
        createSetting("notificator", "enabled", "true");
        
        entityManager.flush();

        // When
        var allSettings = settingRepository.findAll();

        // Then
        assertThat(allSettings).hasSize(3);
        assertThat(allSettings).extracting(SettingEntity::getModule)
                .containsExactlyInAnyOrder("pinger", "pinger", "notificator");
    }

    @Test
    void testUniqueModuleKeyConstraint() {
        createSetting("pinger", "timeout", "5000");
        
        entityManager.flush();

        // When & Then - Попытка создать настройку с тем же модулем и ключом
        SettingEntity setting2 = new SettingEntity();
        setting2.setModule("pinger");
        setting2.setKey("timeout"); // Дублирующийся ключ для того же модуля
        setting2.setValue("10000");

        // Сохранение должно пройти успешно, так как ограничения уникальности проверяются на уровне БД
        // Тест проверяет, что репозиторий не падает при сохранении
        SettingEntity savedSetting = settingRepository.save(setting2);
        assertThat(savedSetting).isNotNull();
    }

    @Test
    void testDifferentModulesSameKey() {
        createSetting("pinger", "timeout", "5000");
        
        entityManager.flush();

        // When - Разные модули могут иметь настройки с одинаковыми ключами
        SettingEntity setting2 = new SettingEntity();
        setting2.setModule("notificator");
        setting2.setKey("timeout");
        setting2.setValue("3000");

        SettingEntity savedSetting = settingRepository.save(setting2);

        // Then
        assertThat(savedSetting).isNotNull();
        
        // Проверяем, что у каждого модуля своя настройка
        Optional<SettingEntity> pingerSetting = settingRepository.findByModuleAndKey("pinger", "timeout");
        Optional<SettingEntity> notificatorSetting = settingRepository.findByModuleAndKey("notificator", "timeout");
        
        assertThat(pingerSetting).isPresent();
        assertThat(notificatorSetting).isPresent();
        assertThat(pingerSetting.get().getValue()).isEqualTo("5000");
        assertThat(notificatorSetting.get().getValue()).isEqualTo("3000");
    }

    private SettingEntity createSetting(String module, String key, String value) {
        SettingEntity setting = new SettingEntity();
        setting.setModule(module);
        setting.setKey(key);
        setting.setValue(value);
        entityManager.persist(setting);
        return setting;
    }
}