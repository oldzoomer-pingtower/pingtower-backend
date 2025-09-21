package ru.oldzoomer.pingtower.notificator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.oldzoomer.pingtower.notificator.dto.NotificationChannelDTO;
import ru.oldzoomer.pingtower.notificator.entity.NotificationChannelEntity;
import ru.oldzoomer.pingtower.notificator.mapper.NotificationChannelMapper;
import ru.oldzoomer.pingtower.notificator.repository.NotificationChannelRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelManagementService {

    private final NotificationChannelRepository notificationChannelRepository;
    private final NotificationChannelMapper notificationChannelMapper;

    /**
     * Получить список каналов уведомлений
     *
     * @param page номер страницы
     * @param size количество элементов на странице
     * @param type фильтр по типу канала
     * @return список каналов уведомлений
     */
    public Map<String, Object> getChannels(int page, int size, String type) {
        log.info("Получение списка каналов уведомлений, страница: {}, размер: {}, тип: {}", page, size, type);
        
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<NotificationChannelEntity> pageResult;
        
        if (type != null && !type.isEmpty()) {
            pageResult = notificationChannelRepository.findByType(type, pageable);
        } else {
            pageResult = notificationChannelRepository.findAll(pageable);
        }
        
        List<NotificationChannelDTO> channels = pageResult.getContent().stream()
                .map(notificationChannelMapper::toDto)
                .toList();
        
        return Map.of(
            "channels", channels,
            "page", page,
            "size", size,
            "total", pageResult.getTotalElements()
        );
    }

    /**
     * Получить информацию о конкретном канале уведомлений
     *
     * @param channelId идентификатор канала
     * @return информация о канале уведомлений
     */
    public NotificationChannelDTO getChannel(String channelId) {
        log.info("Получение информации о канале уведомлений с ID: {}", channelId);
        Optional<NotificationChannelEntity> entity = notificationChannelRepository.findById(channelId);
        return entity.map(notificationChannelMapper::toDto).orElse(null);
    }

    /**
     * Создать новый канал уведомлений
     *
     * @param channel канал уведомлений
     * @return созданный канал уведомлений
     */
    public NotificationChannelDTO createChannel(NotificationChannelDTO channel) {
        log.info("Создание нового канала уведомлений: {}", channel);
        
        NotificationChannelEntity entity = notificationChannelMapper.toEntity(channel);
        
        // Генерируем ID если не задан
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId(java.util.UUID.randomUUID().toString());
        }
        
        // Устанавливаем временные метки
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        
        NotificationChannelEntity savedEntity = notificationChannelRepository.save(entity);
        return notificationChannelMapper.toDto(savedEntity);
    }

    /**
     * Обновить канал уведомлений
     *
     * @param channelId идентификатор канала
     * @param channel обновленный канал уведомлений
     * @return обновленный канал уведомлений
     */
    public NotificationChannelDTO updateChannel(String channelId, NotificationChannelDTO channel) {
        log.info("Обновление канала уведомлений с ID: {}, данные: {}", channelId, channel);
        
        Optional<NotificationChannelEntity> existingEntity = notificationChannelRepository.findById(channelId);
        if (existingEntity.isEmpty()) {
            return null;
        }
        
        NotificationChannelEntity entity = notificationChannelMapper.toEntity(channel);
        entity.setId(channelId);
        entity.setUpdatedAt(LocalDateTime.now());
        // Keep the original createdAt value
        entity.setCreatedAt(existingEntity.get().getCreatedAt());
        
        NotificationChannelEntity savedEntity = notificationChannelRepository.save(entity);
        return notificationChannelMapper.toDto(savedEntity);
    }

    /**
     * Удалить канал уведомлений
     *
     * @param channelId идентификатор канала
     * @return true, если канал был удален, false в противном случае
     */
    public boolean deleteChannel(String channelId) {
        log.info("Удаление канала уведомлений с ID: {}", channelId);
        if (notificationChannelRepository.existsById(channelId)) {
            notificationChannelRepository.deleteById(channelId);
            return true;
        }
        return false;
    }
}