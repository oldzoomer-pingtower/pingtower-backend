package ru.oldzoomer.pingtower.settings_manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.oldzoomer.pingtower.settings_manager.service.UserRoleService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user-roles")
@Tag(name = "Settings Manager - User Roles", description = "Контроллер для управления связями пользователей и ролей")
@RequiredArgsConstructor
public class UserRoleController {

    private final UserRoleService userRoleService;

    @Operation(
        summary = "Получить роли пользователя",
        description = "Получить список идентификаторов ролей для конкретного пользователя"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Список ролей пользователя успешно получен",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = UUID.class, type = "array")
        )
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UUID>> getRolesForUser(
            @Parameter(description = "UUID пользователя", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID userId) {
        List<UUID> roles = userRoleService.getRolesForUser(userId);
        return ResponseEntity.ok(roles);
    }

    @Operation(
        summary = "Получить пользователей с ролью",
        description = "Получить список идентификаторов пользователей, имеющих конкретную роль"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Список пользователей с ролью успешно получен",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = UUID.class, type = "array")
        )
    )
    @GetMapping("/role/{roleId}")
    public ResponseEntity<List<UUID>> getUsersWithRole(
            @Parameter(description = "UUID роли", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID roleId) {
        List<UUID> users = userRoleService.getUsersWithRole(roleId);
        return ResponseEntity.ok(users);
    }

    @Operation(
        summary = "Назначить роль пользователю",
        description = "Назначить конкретную роль конкретному пользователю"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Роль успешно назначена пользователю"
    )
    @PostMapping("/{userId}/{roleId}")
    public ResponseEntity<Void> assignRoleToUser(
            @Parameter(description = "UUID пользователя", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID userId,
            @Parameter(description = "UUID роли", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID roleId) {
        userRoleService.assignRoleToUser(userId, roleId);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Удалить роль у пользователя",
        description = "Удалить конкретную роль у конкретного пользователя"
    )
    @ApiResponse(
        responseCode = "204",
        description = "Роль успешно удалена у пользователя"
    )
    @DeleteMapping("/{userId}/{roleId}")
    public ResponseEntity<Void> removeRoleFromUser(
            @Parameter(description = "UUID пользователя", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID userId,
            @Parameter(description = "UUID роли", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID roleId) {
        userRoleService.removeRoleFromUser(userId, roleId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Удалить все роли у пользователя",
        description = "Удалить все роли у конкретного пользователя"
    )
    @ApiResponse(
        responseCode = "204",
        description = "Все роли успешно удалены у пользователя"
    )
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> removeAllRolesFromUser(
            @Parameter(description = "UUID пользователя", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID userId) {
        userRoleService.removeAllRolesFromUser(userId);
        return ResponseEntity.noContent().build();
    }
}