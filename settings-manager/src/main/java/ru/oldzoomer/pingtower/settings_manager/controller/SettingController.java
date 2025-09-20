package ru.oldzoomer.pingtower.settings_manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import ru.oldzoomer.pingtower.settings_manager.dto.Setting;
import ru.oldzoomer.pingtower.settings_manager.service.SettingService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/settings")
@Tag(name = "Settings Manager", description = "Контроллер для управления настройками системы")
@Validated
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;

    @Operation(
        summary = "Получить все настройки",
        description = "Получить список всех настроек системы"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Список настроек успешно получен",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Setting.class, type = "array")
        )
    )
    @GetMapping
    public ResponseEntity<List<Setting>> getAllSettings() {
        List<Setting> settings = settingService.getAllSettings();
        return ResponseEntity.ok(settings);
    }

    @Operation(
        summary = "Получить настройки по модулю",
        description = "Получить список настроек для конкретного модуля системы"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Список настроек модуля успешно получен",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Setting.class, type = "array")
        )
    )
    @GetMapping("/{module}")
    public ResponseEntity<List<Setting>> getSettingsByModule(
            @Parameter(description = "Название модуля", example = "pinger")
            @PathVariable String module) {
        List<Setting> settings = settingService.getSettingsByModule(module);
        return ResponseEntity.ok(settings);
    }

    @Operation(
        summary = "Получить конкретную настройку",
        description = "Получить конкретную настройку по модулю и ключу"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Настройка успешно получена",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Setting.class)
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Настройка с указанным модулем и ключом не найдена"
    )
    @GetMapping("/{module}/{key}")
    public ResponseEntity<Setting> getSetting(
            @Parameter(description = "Название модуля", example = "pinger")
            @PathVariable String module,
            @Parameter(description = "Ключ настройки", example = "timeout")
            @PathVariable String key) {
        Optional<Setting> setting = settingService.getSetting(module, key);
        return setting.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Создать новую настройку",
        description = "Создать новую настройку в системе"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Настройка успешно создана",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Setting.class)
        )
    )
    @PostMapping
    public ResponseEntity<Setting> createSetting(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Данные настройки для создания",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Setting.class)
                )
            )
            @Valid @RequestBody Setting setting) {
        Setting createdSetting = settingService.createSetting(setting);
        return ResponseEntity.ok(createdSetting);
    }

    @Operation(
        summary = "Обновить настройку",
        description = "Обновить существующую настройку по модулю и ключу"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Настройка успешно обновлена",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Setting.class)
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Настройка с указанным модулем и ключом не найдена"
    )
    @PutMapping("/{module}/{key}")
    public ResponseEntity<Setting> updateSetting(
            @Parameter(description = "Название модуля", example = "pinger")
            @PathVariable String module,
            @Parameter(description = "Ключ настройки", example = "timeout")
            @PathVariable String key,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Обновленные данные настройки",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Setting.class)
                )
            )
            @Valid @RequestBody Setting setting) {
        // Проверяем, что module и key в пути совпадают с module и key в теле запроса
        if (setting.getModule() != null && !setting.getModule().equals(module)) {
            return ResponseEntity.badRequest().build();
        }
        if (setting.getKey() != null && !setting.getKey().equals(key)) {
            return ResponseEntity.badRequest().build();
        }
        
        Setting updatedSetting = settingService.updateSetting(module, key, setting);
        return ResponseEntity.ok(updatedSetting);
    }

    @Operation(
        summary = "Удалить настройку",
        description = "Удалить настройку по модулю и ключу"
    )
    @ApiResponse(
        responseCode = "204",
        description = "Настройка успешно удалена"
    )
    @ApiResponse(
        responseCode = "404",
        description = "Настройка с указанным модулем и ключом не найдена"
    )
    @DeleteMapping("/{module}/{key}")
    public ResponseEntity<Void> deleteSetting(
            @Parameter(description = "Название модуля", example = "pinger")
            @PathVariable String module,
            @Parameter(description = "Ключ настройки", example = "timeout")
            @PathVariable String key) {
        settingService.deleteSetting(module, key);
        return ResponseEntity.noContent().build();
    }
}