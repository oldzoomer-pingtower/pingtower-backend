package ru.oldzoomer.pingtower.pinger.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.oldzoomer.pingtower.pinger.dto.CheckConfigurationDTO;
import ru.oldzoomer.pingtower.pinger.entity.CheckConfigurationEntity;
import ru.oldzoomer.pingtower.pinger.mapper.CheckConfigurationMapper;
import ru.oldzoomer.pingtower.pinger.repository.CheckConfigurationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckManagementService {

    private final CheckConfigurationRepository checkConfigurationRepository;
    private final CheckConfigurationMapper checkConfigurationMapper;

    /**
     * Получить список настроек проверок
     *
     * @param page номер страницы
     * @param size количество элементов на странице
     * @return список настроек проверок
     */
    public Map<String, Object> getChecks(int page, int size) {
        log.info("Получение списка настроек проверок, страница: {}, размер: {}", page, size);
        
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<CheckConfigurationEntity> pageResult = checkConfigurationRepository.findAll(pageable);
        
        List<CheckConfigurationDTO> checks = pageResult.getContent().stream()
                .map(checkConfigurationMapper::toDto)
                .toList();
        
        return Map.of(
            "checks", checks,
            "page", page,
            "size", size,
            "total", pageResult.getTotalElements()
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
        Optional<CheckConfigurationEntity> entity = checkConfigurationRepository.findById(checkId);
        return entity.map(checkConfigurationMapper::toDto).orElse(null);
    }

    /**
     * Создать новую настройку проверки
     *
     * @param check настройка проверки
     * @return созданная настройка проверки
     */
    public CheckConfigurationDTO createCheck(CheckConfigurationDTO check) {
        log.info("Создание новой настройки проверки: {}", check);
        
        CheckConfigurationEntity entity = checkConfigurationMapper.toEntity(check);
        
        // Генерируем ID если не задан
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId(java.util.UUID.randomUUID().toString());
        }
        
        // Устанавливаем временные метки
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        
        CheckConfigurationEntity savedEntity = checkConfigurationRepository.save(entity);
        return checkConfigurationMapper.toDto(savedEntity);
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
        
        Optional<CheckConfigurationEntity> existingEntity = checkConfigurationRepository.findById(checkId);
        if (existingEntity.isEmpty()) {
            return null;
        }
        
        CheckConfigurationEntity entity = checkConfigurationMapper.toEntity(check);
        entity.setId(checkId);
        entity.setUpdatedAt(LocalDateTime.now());
        // Keep the original createdAt value
        entity.setCreatedAt(existingEntity.get().getCreatedAt());
        
        CheckConfigurationEntity savedEntity = checkConfigurationRepository.save(entity);
        return checkConfigurationMapper.toDto(savedEntity);
    }

    /**
     * Удалить настройку проверки
     *
     * @param checkId идентификатор проверки
     * @return true, если настройка была удалена, false в противном случае
     */
    public boolean deleteCheck(String checkId) {
        log.info("Удаление настройки проверки с ID: {}", checkId);
        if (checkConfigurationRepository.existsById(checkId)) {
            checkConfigurationRepository.deleteById(checkId);
            return true;
        }
        return false;
    }
}