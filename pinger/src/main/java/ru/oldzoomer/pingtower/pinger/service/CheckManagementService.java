package ru.oldzoomer.pingtower.pinger.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.oldzoomer.pingtower.pinger.dto.CheckConfigurationDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckManagementService {

    private final Map<String, CheckConfigurationDTO> checks = new ConcurrentHashMap<>();

    /**
     * Получить список настроек проверок
     *
     * @param page номер страницы
     * @param size количество элементов на странице
     * @return список настроек проверок
     */
    public Map<String, Object> getChecks(int page, int size) {
        log.info("Получение списка настроек проверок, страница: {}, размер: {}", page, size);
        
        List<CheckConfigurationDTO> allChecks = checks.values().stream().collect(Collectors.toList());
        
        int total = allChecks.size();
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, total);
        
        List<CheckConfigurationDTO> pageChecks = allChecks.subList(
            Math.min(fromIndex, total), 
            Math.min(toIndex, total)
        );
        
        return Map.of(
            "checks", pageChecks,
            "page", page,
            "size", size,
            "total", total
        );
    }

    /**
     * Получить информацию о конкретной настройке проверки
     *
     * @param checkId идентификатор проверки
     * @return информация о настройке проверки
     */
    public CheckConfigurationDTO getCheck(String checkId) {
        log.info("Получение информации о настройке проверки с ID: {}", checkId);
        return checks.get(checkId);
    }

    /**
     * Создать новую настройку проверки
     *
     * @param check настройка проверки
     * @return созданная настройка проверки
     */
    public CheckConfigurationDTO createCheck(CheckConfigurationDTO check) {
        log.info("Создание новой настройки проверки: {}", check);
        
        // Генерируем ID если не задан
        if (check.getId() == null || check.getId().isEmpty()) {
            check.setId(java.util.UUID.randomUUID().toString());
        }
        
        // Устанавливаем временные метки
        String now = LocalDateTime.now().toString();
        check.setCreatedAt(now);
        check.setUpdatedAt(now);
        
        checks.put(check.getId(), check);
        
        return check;
    }

    /**
     * Обновить настройку проверки
     *
     * @param checkId идентификатор проверки
     * @param check обновленная настройка проверки
     * @return обновленная настройка проверки
     */
    public CheckConfigurationDTO updateCheck(String checkId, CheckConfigurationDTO check) {
        log.info("Обновление настройки проверки с ID: {}, данные: {}", checkId, check);
        
        if (!checks.containsKey(checkId)) {
            return null;
        }
        
        check.setId(checkId);
        check.setUpdatedAt(LocalDateTime.now().toString());
        
        checks.put(checkId, check);
        
        return check;
    }

    /**
     * Удалить настройку проверки
     *
     * @param checkId идентификатор проверки
     * @return true, если настройка была удалена, false в противном случае
     */
    public boolean deleteCheck(String checkId) {
        log.info("Удаление настройки проверки с ID: {}", checkId);
        return checks.remove(checkId) != null;
    }
}