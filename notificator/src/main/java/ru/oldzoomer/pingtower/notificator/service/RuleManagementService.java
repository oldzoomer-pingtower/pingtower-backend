package ru.oldzoomer.pingtower.notificator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.oldzoomer.pingtower.notificator.dto.NotificationRuleDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleManagementService {

    private final Map<String, NotificationRuleDTO> rules = new ConcurrentHashMap<>();

    /**
     * Получить список правил уведомлений
     *
     * @param page номер страницы
     * @param size количество элементов на странице
     * @return список правил уведомлений
     */
    public Map<String, Object> getRules(int page, int size) {
        log.info("Получение списка правил уведомлений, страница: {}, размер: {}", page, size);
        
        List<NotificationRuleDTO> allRules = rules.values().stream().collect(Collectors.toList());
        
        int total = allRules.size();
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, total);
        
        List<NotificationRuleDTO> pageRules = allRules.subList(
            Math.min(fromIndex, total), 
            Math.min(toIndex, total)
        );
        
        return Map.of(
            "rules", pageRules,
            "page", page,
            "size", size,
            "total", total
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
        return rules.get(ruleId);
    }

    /**
     * Создать новое правило уведомлений
     *
     * @param rule правило уведомлений
     * @return созданное правило уведомлений
     */
    public NotificationRuleDTO createRule(NotificationRuleDTO rule) {
        log.info("Создание нового правила уведомлений: {}", rule);
        
        // Генерируем ID если не задан
        if (rule.getId() == null || rule.getId().isEmpty()) {
            rule.setId(java.util.UUID.randomUUID().toString());
        }
        
        // Устанавливаем временные метки
        String now = LocalDateTime.now().toString();
        rule.setCreatedAt(now);
        rule.setUpdatedAt(now);
        
        rules.put(rule.getId(), rule);
        
        return rule;
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
        
        if (!rules.containsKey(ruleId)) {
            return null;
        }
        
        rule.setId(ruleId);
        rule.setUpdatedAt(LocalDateTime.now().toString());
        
        rules.put(ruleId, rule);
        
        return rule;
    }

    /**
     * Удалить правило уведомлений
     *
     * @param ruleId идентификатор правила
     * @return true, если правило было удалено, false в противном случае
     */
    public boolean deleteRule(String ruleId) {
        log.info("Удаление правила уведомлений с ID: {}", ruleId);
        return rules.remove(ruleId) != null;
    }
}