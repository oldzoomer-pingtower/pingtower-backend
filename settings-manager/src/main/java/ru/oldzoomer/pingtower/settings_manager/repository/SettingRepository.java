package ru.oldzoomer.pingtower.settings_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.oldzoomer.pingtower.settings_manager.entity.SettingEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SettingRepository extends JpaRepository<SettingEntity, UUID> {
    List<SettingEntity> findByModule(String module);
    Optional<SettingEntity> findByModuleAndKey(String module, String key);
    boolean existsByModuleAndKey(String module, String key);
}