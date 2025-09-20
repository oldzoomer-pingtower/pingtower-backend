package ru.oldzoomer.pingtower.statistics.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.oldzoomer.pingtower.statistics.dto.CheckResult;
import ru.oldzoomer.pingtower.statistics.service.StatisticsRetrievalService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "Контроллер для работы со статистикой мониторинга")
public class StatisticsController {
    private final StatisticsRetrievalService statisticsRetrievalService;
    
    @Operation(
        summary = "Получение последних результатов проверки",
        description = "Получить последний результат проверки по идентификатору проверки"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Последний результат проверки успешно получен",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CheckResult.class)
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Результаты проверки не найдены"
    )
    @GetMapping("/checks/{checkId}/latest")
    public ResponseEntity<CheckResult> getLatestCheckResult(
            @Parameter(description = "Идентификатор проверки", example = "check-12345")
            @PathVariable String checkId) {
        try {
            CheckResult result = statisticsRetrievalService.getLatestCheckResult(checkId);
            if (result != null) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Failed to get latest check result for checkId: {}", checkId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @Operation(
        summary = "Получение истории результатов проверки",
        description = "Получить историю результатов проверки с возможностью фильтрации по времени"
    )
    @ApiResponse(
        responseCode = "200",
        description = "История результатов проверки успешно получена",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CheckResult.class, type = "array")
        )
    )
    @GetMapping("/checks/{checkId}/history")
    public ResponseEntity<List<CheckResult>> getCheckHistory(
            @Parameter(description = "Идентификатор проверки", example = "check-12345")
            @PathVariable String checkId,
            @Parameter(description = "Начальная дата для фильтрации", example = "2024-01-15T10:30:00")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @Parameter(description = "Конечная дата для фильтрации", example = "2024-01-16T10:30:00")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @Parameter(description = "Ограничение на количество записей", example = "100")
            @RequestParam(defaultValue = "100") int limit,
            @Parameter(description = "Смещение для пагинации", example = "0")
            @RequestParam(defaultValue = "0") int offset) {
        try {
            List<CheckResult> history = statisticsRetrievalService.getCheckHistory(checkId, from, to, limit, offset);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            log.error("Failed to get check history for checkId: {}", checkId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @Operation(
        summary = "Получение агрегированных данных по проверке",
        description = "Получить агрегированные данные по проверке за указанный интервал времени"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Агрегированные данные успешно получены"
    )
    @ApiResponse(
        responseCode = "404",
        description = "Данные для агрегации не найдены"
    )
    @GetMapping("/checks/{checkId}/aggregated")
    public ResponseEntity<Object> getAggregatedData(
            @Parameter(description = "Идентификатор проверки", example = "check-12345")
            @PathVariable String checkId,
            @Parameter(description = "Интервал агрегации", example = "hourly")
            @RequestParam String interval,
            @Parameter(description = "Начальная дата для агрегации", example = "2024-01-15T10:30:00")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @Parameter(description = "Конечная дата для агрегации", example = "2024-01-16T10:30:00")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        try {
            Object aggregatedData = statisticsRetrievalService.getAggregatedData(checkId, interval, from, to);
            if (aggregatedData != null) {
                return ResponseEntity.ok(aggregatedData);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Failed to get aggregated data for checkId: {}, interval: {}", checkId, interval, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @Operation(
        summary = "Получение данных для дашборда",
        description = "Получить агрегированные данные для отображения на дашборде"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Данные для дашборда успешно получены"
    )
    @GetMapping("/dashboard")
    public ResponseEntity<Object> getDashboardData() {
        try {
            Object dashboardData = statisticsRetrievalService.getDashboardData();
            return ResponseEntity.ok(dashboardData);
        } catch (Exception e) {
            log.error("Failed to get dashboard data", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}