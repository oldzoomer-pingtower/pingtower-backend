package ru.oldzoomer.pingtower.settings_manager.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.oldzoomer.pingtower.settings_manager.entity.AuditLogEntity;
import ru.oldzoomer.pingtower.settings_manager.repository.AuditLogRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Cacheable(value = "auditLogs", key = "#settingId")
    public List<AuditLogEntity> getAuditLogForSetting(UUID settingId) {
        return auditLogRepository.findBySettingId(settingId);
    }

    @Cacheable(value = "auditLogs", key = "#userId")
    public List<AuditLogEntity> getAuditLogForUser(UUID userId) {
        return auditLogRepository.findByUserId(userId);
    }

    @Transactional
    public void logSettingChange(UUID settingId, UUID userId, String action, String oldValue, String newValue) {
        AuditLogEntity auditLog = new AuditLogEntity();
        auditLog.setSettingId(settingId);
        auditLog.setUserId(userId);
        auditLog.setAction(action);
        auditLog.setOldValue(oldValue);
        auditLog.setNewValue(newValue);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(auditLog);
    }
}