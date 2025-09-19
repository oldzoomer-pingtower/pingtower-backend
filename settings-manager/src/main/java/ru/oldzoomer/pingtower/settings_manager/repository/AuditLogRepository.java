package ru.oldzoomer.pingtower.settings_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.oldzoomer.pingtower.settings_manager.entity.AuditLogEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, UUID> {
    List<AuditLogEntity> findBySettingId(UUID settingId);
    List<AuditLogEntity> findByUserId(UUID userId);
}