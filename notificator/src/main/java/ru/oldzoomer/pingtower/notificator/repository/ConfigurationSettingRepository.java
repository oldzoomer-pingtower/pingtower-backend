package ru.oldzoomer.pingtower.notificator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.oldzoomer.pingtower.notificator.entity.ConfigurationSettingEntity;

import java.util.Optional;

@Repository
public interface ConfigurationSettingRepository extends JpaRepository<ConfigurationSettingEntity, Long> {
    Optional<ConfigurationSettingEntity> findByKey(String key);
}