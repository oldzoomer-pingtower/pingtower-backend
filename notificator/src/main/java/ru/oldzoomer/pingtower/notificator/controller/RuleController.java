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
import ru.oldzoomer.pingtower.notificator.dto.NotificationRuleDTO;
import ru.oldzoomer.pingtower.notificator.service.RuleManagementService;

import jakarta.validation.Valid;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/notificator/rules")
@RequiredArgsConstructor
@Tag(name = "Notificator Rules", description = "Контроллер для управления правилами уведомлений Notificator")
public class RuleController {

    private final RuleManagementService ruleManagementService;

    /**
     * Получить список правил уведомлений
     *
     * @param page номер страницы (по умолчанию 1)
     * @param size количество элементов на странице (по умолчанию 20)
     * @return список правил уведомлений
     */
    @Operation(
        summary = "Получить список правил уведомлений",
        description = "Получить список правил уведомлений с возможностью пагинации"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Список правил уведомлений успешно получен",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Map.class)
        )
    )
    @GetMapping
    public ResponseEntity<Map<String, Object>> getRules(
            @Parameter(description = "Номер страницы (по умолчанию 1)") 
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Количество элементов на странице (по умолчанию 20)") 
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Получение списка правил уведомлений, страница: {}, размер: {}", page, size);
        
        Map<String, Object> response = ruleManagementService.getRules(page, size);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Получить информацию о конкретном правиле уведомлений
     *
     * @param ruleId идентификатор правила
     * @return информация о правиле уведомлений
     */
    @Operation(
        summary = "Получить информацию о конкретном правиле уведомлений",
        description = "Получить информацию о конкретном правиле уведомлений по его идентификатору"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Информация о правиле уведомлений успешно получена",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = NotificationRuleDTO.class)
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Правило уведомлений с указанным идентификатором не найдено"
    )
    @GetMapping("/{ruleId}")
    public ResponseEntity<NotificationRuleDTO> getRule(
            @Parameter(description = "Идентификатор правила") 
            @PathVariable String ruleId) {
        log.info("Получение информации о правиле уведомлений с ID: {}", ruleId);
        
        NotificationRuleDTO rule = ruleManagementService.getRule(ruleId);
        if (rule == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(rule);
    }

    /**
     * Создать новое правило уведомлений
     *
     * @param rule правило уведомлений
     * @return созданное правило уведомлений
     */
    @Operation(
        summary = "Создать новое правило уведомлений",
        description = "Создать новое правило уведомлений"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Правило уведомлений успешно создано",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = NotificationRuleDTO.class)
        )
    )
    @PostMapping
    public ResponseEntity<NotificationRuleDTO> createRule(
            @Parameter(description = "Правило уведомлений")
            @Valid @RequestBody NotificationRuleDTO rule) {
        log.info("Создание нового правила уведомлений: {}", rule);
        
        NotificationRuleDTO createdRule = ruleManagementService.createRule(rule);
        
        return ResponseEntity.ok(createdRule);
    }

    /**
     * Обновить правило уведомлений
     *
     * @param ruleId идентификатор правила
     * @param rule обновленное правило уведомлений
     * @return обновленное правило уведомлений
     */
    @Operation(
        summary = "Обновить правило уведомлений",
        description = "Обновить правило уведомлений по его идентификатору"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Правило уведомлений успешно обновлено",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = NotificationRuleDTO.class)
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Правило уведомлений с указанным идентификатором не найдено"
    )
    @PutMapping("/{ruleId}")
    public ResponseEntity<NotificationRuleDTO> updateRule(
            @Parameter(description = "Идентификатор правила")
            @PathVariable String ruleId,
            @Parameter(description = "Обновленное правило уведомлений")
            @Valid @RequestBody NotificationRuleDTO rule) {
        log.info("Обновление правила уведомлений с ID: {}, данные: {}", ruleId, rule);
        
        NotificationRuleDTO updatedRule = ruleManagementService.updateRule(ruleId, rule);
        if (updatedRule == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(updatedRule);
    }

    /**
     * Удалить правило уведомлений
     *
     * @param ruleId идентификатор правила
     * @return сообщение об успешном удалении
     */
    @Operation(
        summary = "Удалить правило уведомлений",
        description = "Удалить правило уведомлений по его идентификатору"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Правило уведомлений успешно удалено",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Map.class)
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Правило уведомлений с указанным идентификатором не найдено"
    )
    @DeleteMapping("/{ruleId}")
    public ResponseEntity<Map<String, String>> deleteRule(
            @Parameter(description = "Идентификатор правила") 
            @PathVariable String ruleId) {
        log.info("Удаление правила уведомлений с ID: {}", ruleId);
        
        boolean deleted = ruleManagementService.deleteRule(ruleId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(Map.of("message", "Rule deleted successfully"));
    }
}