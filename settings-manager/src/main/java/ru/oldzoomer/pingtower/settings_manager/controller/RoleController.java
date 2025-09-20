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
import ru.oldzoomer.pingtower.settings_manager.dto.Role;
import ru.oldzoomer.pingtower.settings_manager.service.RoleService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roles")
@Tag(name = "Settings Manager - Roles", description = "Контроллер для управления ролями пользователей")
@Validated
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Operation(
        summary = "Получить все роли",
        description = "Получить список всех ролей системы"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Список ролей успешно получен",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Role.class, type = "array")
        )
    )
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @Operation(
        summary = "Получить роль по ID",
        description = "Получить информацию о конкретной роли по ее идентификатору"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Информация о роли успешно получена",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Role.class)
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Роль с указанным ID не найдена"
    )
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRole(
            @Parameter(description = "UUID роли", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        Optional<Role> role = roleService.getRoleById(id);
        return role.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Создать новую роль",
        description = "Создать новую роль в системе"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Роль успешно создана",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Role.class)
        )
    )
    @ApiResponse(
        responseCode = "400",
        description = "Неверные данные роли"
    )
    @PostMapping
    public ResponseEntity<Role> createRole(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Данные роли для создания",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Role.class)
                )
            )
            @Valid @RequestBody Role role) {
        try {
            Role createdRole = roleService.createRole(role);
            return ResponseEntity.ok(createdRole);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
        summary = "Обновить роль",
        description = "Обновить информацию о роли по ее идентификатору"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Роль успешно обновлена",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Role.class)
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Роль с указанным ID не найдена"
    )
    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(
            @Parameter(description = "UUID роли", example = "123e4567-e89b-12d3-a456-42614174000")
            @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Обновленные данные роли",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Role.class)
                )
            )
            @RequestBody Role role) {
        // Проверяем, что ID в пути совпадает с ID в теле запроса
        if (role.getId() != null && !role.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }
        
        // Для частичных обновлений валидируем только те поля, которые предоставлены
        if (role.getName() != null && role.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            Role updatedRole = roleService.updateRole(id, role);
            return ResponseEntity.ok(updatedRole);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Удалить роль",
        description = "Удалить роль по ее идентификатору"
    )
    @ApiResponse(
        responseCode = "204",
        description = "Роль успешно удалена"
    )
    @ApiResponse(
        responseCode = "404",
        description = "Роль с указанным ID не найдена"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(
            @Parameter(description = "UUID роли", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        try {
            roleService.deleteRole(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}