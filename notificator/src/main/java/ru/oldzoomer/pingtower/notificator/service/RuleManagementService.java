package ru.oldzoomer.pingtower.notificator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.oldzoomer.pingtower.notificator.dto.NotificationRuleDTO;
import ru.oldzoomer.pingtower.notificator.entity.NotificationRuleEntity;
import ru.oldzoomer.pingtower.notificator.mapper.NotificationRuleMapper;
import ru.oldzoomer.pingtower.notificator.repository.NotificationRuleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleManagementService {

    private final NotificationRuleRepository notificationRuleRepository;
    private final NotificationRuleMapper notificationRuleMapper;

    /**
     * Получить список правил уведомлений
     *
     * @param page номер страницы
     * @param size количество элементов на странице
     * @return список правил уведомлений
     */
    public Map<String, Object> getRules(int page, int size) {
        log.info("Получение списка правил уведомлений, страница: {}, размер: {}", page, size);
        
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<NotificationRuleEntity> pageResult = notificationRuleRepository.findAll(pageable);
        
        List<NotificationRuleDTO> rules = pageResult.getContent().stream()
                .map(notificationRuleMapper::toDto)
                .toList();
        
        return Map.of(
            "rules", rules,
            "page", page,
            "size", size,
            "total", pageResult.getTotalElements()
        );
    }

    /**
     * Получить информацию о конкретном правиле уведомлений
     *
     * @param ruleId идентификатор правила
     * @return информация о правиле уведомлений
     */
    public NotificationRuleDTO getRule(String ruleId) {
        log.info("Получение информации о правиле уведомлений с ID: {}", ruleId);
        Optional<NotificationRuleEntity> entity = notificationRuleRepository.findById(ruleId);
        return entity.map(notificationRuleMapper::toDto).orElse(null);
    }

    /**
     * Создать новое правило уведомлений
     *
     * @param rule правило уведомлений
     * @return созданное правило уведомлений
     */
    public NotificationRuleDTO createRule(NotificationRuleDTO rule) {
        log.info("Создание нового правила уведомлений: {}", rule);
        
        NotificationRuleEntity entity = notificationRuleMapper.toEntity(rule);
        
        // Генерируем ID если не задан
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId(java.util.UUID.randomUUID().toString());
        }
        
        // Устанавливаем временные метки
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        
        NotificationRuleEntity savedEntity = notificationRuleRepository.save(entity);
        return notificationRuleMapper.toDto(savedEntity);
    }

    /**
     * Обновить правило уведомлений
     *
     * @param ruleId идентификатор правила
     * @param rule обновленное правило уведомлений
     * @return обновленное правило уведомлений
     */
    public NotificationRuleDTO updateRule(String ruleId, NotificationRuleDTO rule) {
        log.info("Обновление правила уведомлений с ID: {}, данные: {}", ruleId, rule);
        
        Optional<NotificationRuleEntity> existingEntity = notificationRuleRepository.findById(ruleId);
        if (existingEntity.isEmpty()) {
            return null;
        }
        
        NotificationRuleEntity entity = notificationRuleMapper.toEntity(rule);
        entity.setId(ruleId);
        entity.setUpdatedAt(LocalDateTime.now());
        // Keep the original createdAt value
        entity.setCreatedAt(existingEntity.get().getCreatedAt());
        
        NotificationRuleEntity savedEntity = notificationRuleRepository.save(entity);
        return notificationRuleMapper.toDto(savedEntity);
    }

    /**
     * Удалить правило уведомлений
     *
     * @param ruleId идентификатор правила
     * @return true, если правило было удалено, false в противном случае
     */
    public boolean deleteRule(String ruleId) {
        log.info("Удаление правила уведомлений с ID: {}", ruleId);
        if (notificationRuleRepository.existsById(ruleId)) {
            notificationRuleRepository.deleteById(ruleId);
            return true;
        }
        return false;
    }
}