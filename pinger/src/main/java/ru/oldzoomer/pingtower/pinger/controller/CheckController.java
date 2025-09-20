package ru.oldzoomer.pingtower.pinger.controller;

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
import ru.oldzoomer.pingtower.pinger.dto.CheckConfigurationDTO;
import ru.oldzoomer.pingtower.pinger.service.CheckManagementService;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/pinger/checks")
@RequiredArgsConstructor
@Tag(name = "Pinger Checks", description = "Контроллер для управления проверками доступности ресурсов")
public class CheckController {

    private final CheckManagementService checkManagementService;

    /**
     * Получить список настроек проверок Pinger
     *
     * @param page номер страницы (по умолчанию 1)
     * @param size количество элементов на странице (по умолчанию 20)
     * @return список настроек проверок
     */
    @GetMapping
    @Operation(
        summary = "Получить список настроек проверок",
        description = "Возвращает пагинированный список всех настроек проверок доступности ресурсов",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Список настроек проверок успешно получен",
                content = @Content(mediaType = "application/json")
            )
        }
    )
    public ResponseEntity<Map<String, Object>> getChecks(
            @Parameter(description = "Номер страницы", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Количество элементов на странице", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Получение списка настроек проверок Pinger, страница: {}, размер: {}", page, size);
        
        Map<String, Object> response = checkManagementService.getChecks(page, size);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Получить информацию о конкретной настройке проверки Pinger
     *
     * @param checkId идентификатор проверки
     * @return информация о настройке проверки
     */
    @GetMapping("/{checkId}")
    @Operation(
        summary = "Получить информацию о проверке",
        description = "Возвращает детальную информацию о конкретной настройке проверки",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Информация о проверке успешно получена",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CheckConfigurationDTO.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Проверка с указанным ID не найдена"
            )
        }
    )
    public ResponseEntity<CheckConfigurationDTO> getCheck(
            @Parameter(description = "Идентификатор проверки", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable String checkId) {
        log.info("Получение информации о настройке проверки Pinger с ID: {}", checkId);
        
        CheckConfigurationDTO check = checkManagementService.getCheck(checkId);
        if (check == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(check);
    }

    /**
     * Создать новую настройку проверки Pinger
     *
     * @param checkConfiguration конфигурация проверки
     * @return созданная конфигурация проверки
     */
    @PostMapping
    @Operation(
        summary = "Создать новую проверку",
        description = "Создает новую настройку проверки доступности ресурса",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Проверка успешно создана",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CheckConfigurationDTO.class)
                )
            )
        }
    )
    public ResponseEntity<CheckConfigurationDTO> createCheck(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Конфигурация проверки для создания",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CheckConfigurationDTO.class)
                )
            )
            @RequestBody CheckConfigurationDTO checkConfiguration) {
        log.info("Создание новой настройки проверки Pinger: {}", checkConfiguration);
        
        CheckConfigurationDTO createdCheck = checkManagementService.createCheck(checkConfiguration);
        
        return ResponseEntity.ok(createdCheck);
    }

    /**
     * Обновить настройку проверки Pinger
     *
     * @param checkId идентификатор проверки
     * @param checkConfiguration обновленная конфигурация проверки
     * @return обновленная конфигурация проверки
     */
    @PutMapping("/{checkId}")
    @Operation(
        summary = "Обновить проверку",
        description = "Обновляет существующую настройку проверки доступности ресурса",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Проверка успешно обновлена",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CheckConfigurationDTO.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Проверка с указанным ID не найдена"
            )
        }
    )
    public ResponseEntity<CheckConfigurationDTO> updateCheck(
            @Parameter(description = "Идентификатор проверки", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable String checkId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Обновленная конфигурация проверки",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CheckConfigurationDTO.class)
                )
            )
            @RequestBody CheckConfigurationDTO checkConfiguration) {
        log.info("Обновление настройки проверки Pinger с ID: {}, данные: {}", checkId, checkConfiguration);
        
        CheckConfigurationDTO updatedCheck = checkManagementService.updateCheck(checkId, checkConfiguration);
        if (updatedCheck == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(updatedCheck);
    }

    /**
     * Удалить настройку проверки Pinger
     *
     * @param checkId идентификатор проверки
     * @return сообщение об успешном удалении
     */
    @DeleteMapping("/{checkId}")
    @Operation(
        summary = "Удалить проверку",
        description = "Удаляет настройку проверки доступности ресурса",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Проверка успешно удалена",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Проверка с указанным ID не найдена"
            )
        }
    )
    public ResponseEntity<Map<String, String>> deleteCheck(
            @Parameter(description = "Идентификатор проверки", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable String checkId) {
        log.info("Удаление настройки проверки Pinger с ID: {}", checkId);
        
        boolean deleted = checkManagementService.deleteCheck(checkId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(Map.of("message", "Check deleted successfully"));
    }
}