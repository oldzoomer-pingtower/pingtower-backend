package ru.oldzoomer.pingtower.notificator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.oldzoomer.pingtower.notificator.dto.NotificationChannelDTO;
import ru.oldzoomer.pingtower.notificator.service.ChannelManagementService;

import jakarta.validation.Valid;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/notificator/channels")
@RequiredArgsConstructor
@Tag(name = "Notificator Channels", description = "Контроллер для управления каналами уведомлений Notificator")
public class ChannelController {

    private final ChannelManagementService channelManagementService;

    /**
     * Получить список каналов уведомлений
     *
     * @param page номер страницы (по умолчанию 1)
     * @param size количество элементов на странице (по умолчанию 20)
     * @param type фильтр по типу канала (EMAIL, TELEGRAM, WEBHOOK)
     * @return список каналов уведомлений
     */
    @Operation(
        summary = "Получить список каналов уведомлений",
        description = "Получить список каналов уведомлений с возможностью пагинации и фильтрации по типу"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Список каналов уведомлений успешно получен",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Map.class)
        )
    )
    @GetMapping
    public ResponseEntity<Map<String, Object>> getChannels(
            @Parameter(description = "Номер страницы (по умолчанию 1)")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Количество элементов на странице (по умолчанию 20)")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Фильтр по типу канала (EMAIL, TELEGRAM, WEBHOOK)")
            @RequestParam(required = false) String type) {
        
        log.info("Получение списка каналов уведомлений, страница: {}, размер: {}, тип: {}", page, size, type);
        
        Map<String, Object> response = channelManagementService.getChannels(page, size, type);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Получить информацию о конкретном канале уведомлений
     *
     * @param channelId идентификатор канала
     * @return информация о канале уведомлений
     */
    @Operation(
        summary = "Получить информацию о конкретном канале уведомлений",
        description = "Получить информацию о конкретном канале уведомлений по его идентификатору"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Информация о канале уведомлений успешно получена",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = NotificationChannelDTO.class)
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Канал уведомлений с указанным идентификатором не найден"
    )
    @GetMapping("/{channelId}")
    public ResponseEntity<NotificationChannelDTO> getChannel(
            @Parameter(description = "Идентификатор канала")
            @PathVariable String channelId) {
        log.info("Получение информации о канале уведомлений с ID: {}", channelId);
        
        NotificationChannelDTO channel = channelManagementService.getChannel(channelId);
        if (channel == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(channel);
    }

    /**
     * Создать новый канал уведомлений
     *
     * @param channel канал уведомлений
     * @return созданный канал уведомлений
     */
    @Operation(
        summary = "Создать новый канал уведомлений",
        description = "Создать новый канал уведомлений"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Канал уведомлений успешно создан",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = NotificationChannelDTO.class)
        )
    )
    @PostMapping
    public ResponseEntity<NotificationChannelDTO> createChannel(
            @Parameter(description = "Канал уведомлений")
            @Valid @RequestBody NotificationChannelDTO channel) {
        log.info("Создание нового канала уведомлений: {}", channel);
        
        NotificationChannelDTO createdChannel = channelManagementService.createChannel(channel);
        
        return ResponseEntity.ok(createdChannel);
    }

    /**
     * Обновить канал уведомлений
     *
     * @param channelId идентификатор канала
     * @param channel обновленный канал уведомлений
     * @return обновленный канал уведомлений
     */
    @Operation(
        summary = "Обновить канал уведомлений",
        description = "Обновить канал уведомлений по его идентификатору"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Канал уведомлений успешно обновлен",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = NotificationChannelDTO.class)
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Канал уведомлений с указанным идентификатором не найден"
    )
    @PutMapping("/{channelId}")
    public ResponseEntity<NotificationChannelDTO> updateChannel(
            @Parameter(description = "Идентификатор канала")
            @PathVariable String channelId,
            @Parameter(description = "Обновленный канал уведомлений")
            @Valid @RequestBody NotificationChannelDTO channel) {
        log.info("Обновление канала уведомлений с ID: {}, данные: {}", channelId, channel);
        
        NotificationChannelDTO updatedChannel = channelManagementService.updateChannel(channelId, channel);
        if (updatedChannel == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(updatedChannel);
    }

    /**
     * Удалить канал уведомлений
     *
     * @param channelId идентификатор канала
     * @return сообщение об успешном удалении
     */
    @Operation(
        summary = "Удалить канал уведомлений",
        description = "Удалить канал уведомлений по его идентификатору"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Канал уведомлений успешно удален",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Map.class)
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Канал уведомлений с указанным идентификатором не найден"
    )
    @DeleteMapping("/{channelId}")
    public ResponseEntity<Map<String, String>> deleteChannel(
            @Parameter(description = "Идентификатор канала")
            @PathVariable String channelId) {
        log.info("Удаление канала уведомлений с ID: {}", channelId);
        
        boolean deleted = channelManagementService.deleteChannel(channelId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(Map.of("message", "Channel deleted successfully"));
    }
}