package ru.oldzoomer.pingtower.statistics.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.oldzoomer.pingtower.statistics.dto.CheckResult;
import ru.oldzoomer.pingtower.statistics.service.StatisticsRetrievalService;

@Slf4j
@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsRetrievalService statisticsRetrievalService;
    
    /**
     * Получение последних результатов проверки
     * @param checkId идентификатор проверки
     * @return последние результаты проверки
     */
    @GetMapping("/checks/{checkId}/latest")
    public ResponseEntity<CheckResult> getLatestCheckResult(@PathVariable String checkId) {
        try {
            // Получаем из сервиса
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
    
    /**
     * Получение истории результатов проверки
     * @param checkId идентификатор проверки
     * @param from начальная дата
     * @param to конечная дата
     * @param limit ограничение на количество записей
     * @param offset смещение
     * @return история результатов проверки
     */
    @GetMapping("/checks/{checkId}/history")
    public ResponseEntity<List<CheckResult>> getCheckHistory(
            @PathVariable String checkId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        try {
            List<CheckResult> history = statisticsRetrievalService.getCheckHistory(checkId, from, to, limit, offset);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            log.error("Failed to get check history for checkId: {}", checkId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Получение агрегированных данных по проверке
     * @param checkId идентификатор проверки
     * @param interval интервал агрегации
     * @param from начальная дата
     * @param to конечная дата
     * @return агрегированные данные
     */
    @GetMapping("/checks/{checkId}/aggregated")
    public ResponseEntity<Object> getAggregatedData(
            @PathVariable String checkId,
            @RequestParam String interval,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        try {
            // Получаем из сервиса
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
    
    /**
     * Получение данных для дашборда
     * @return данные для дашборда
     */
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