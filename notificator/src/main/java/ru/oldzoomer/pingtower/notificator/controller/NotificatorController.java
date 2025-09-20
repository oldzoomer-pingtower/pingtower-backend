package ru.oldzoomer.pingtower.notificator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/notificator")
@Tag(name = "Notificator", description = "Контроллер для получения информации о состоянии сервиса Notificator")
public class NotificatorController {

    @Operation(
        summary = "Получить информацию о состоянии сервиса Notificator",
        description = "Получить информацию о состоянии сервиса Notificator"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Информация о состоянии сервиса успешно получена"
    )
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        log.info("Получение информации о состоянии сервиса Notificator");
        
        Map<String, Object> healthInfo = Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now(),
            "service", "Notificator",
            "version", "1.0.0"
        );
        
        return ResponseEntity.ok(healthInfo);
    }

    @Operation(
        summary = "Получить статистику сервиса Notificator",
        description = "Получить статистику сервиса Notificator"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Статистика сервиса успешно получена"
    )
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("Получение статистики сервиса Notificator");
        
        Map<String, Object> stats = Map.of(
            "activeChannels", 0,
            "activeRules", 0,
            "notificationsSent", 0,
            "notificationsFailed", 0,
            "timestamp", LocalDateTime.now()
        );
        
        return ResponseEntity.ok(stats);
    }
}