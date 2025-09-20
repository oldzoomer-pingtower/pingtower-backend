package ru.oldzoomer.pingtower.notificator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.oldzoomer.pingtower.notificator.dto.NotificationChannelDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelManagementService {

    private final Map<String, NotificationChannelDTO> channels = new ConcurrentHashMap<>();

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
        
        List<NotificationChannelDTO> allChannels = channels.values().stream()
                .filter(channel -> type == null || channel.getType().equals(type))
                .collect(Collectors.toList());
        
        int total = allChannels.size();
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, total);
        
        List<NotificationChannelDTO> pageChannels = allChannels.subList(
            Math.min(fromIndex, total), 
            Math.min(toIndex, total)
        );
        
        return Map.of(
            "channels", pageChannels,
            "page", page,
            "size", size,
            "total", total
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
        return channels.get(channelId);
    }

    /**
     * Создать новый канал уведомлений
     *
     * @param channel канал уведомлений
     * @return созданный канал уведомлений
     */
    public NotificationChannelDTO createChannel(NotificationChannelDTO channel) {
        log.info("Создание нового канала уведомлений: {}", channel);
        
        // Генерируем ID если не задан
        if (channel.getId() == null || channel.getId().isEmpty()) {
            channel.setId(java.util.UUID.randomUUID().toString());
        }
        
        // Устанавливаем временные метки
        String now = LocalDateTime.now().toString();
        channel.setCreatedAt(now);
        channel.setUpdatedAt(now);
        
        channels.put(channel.getId(), channel);
        
        return channel;
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
        
        if (!channels.containsKey(channelId)) {
            return null;
        }
        
        channel.setId(channelId);
        channel.setUpdatedAt(LocalDateTime.now().toString());
        
        channels.put(channelId, channel);
        
        return channel;
    }

    /**
     * Удалить канал уведомлений
     *
     * @param channelId идентификатор канала
     * @return true, если канал был удален, false в противном случае
     */
    public boolean deleteChannel(String channelId) {
        log.info("Удаление канала уведомлений с ID: {}", channelId);
        return channels.remove(channelId) != null;
    }
}