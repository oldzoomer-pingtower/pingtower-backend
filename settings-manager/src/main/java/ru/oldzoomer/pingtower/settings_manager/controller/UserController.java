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
import ru.oldzoomer.pingtower.settings_manager.dto.User;
import ru.oldzoomer.pingtower.settings_manager.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Settings Manager - Users", description = "Контроллер для управления пользователями системы")
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
        summary = "Получить всех пользователей",
        description = "Получить список всех пользователей системы"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Список пользователей успешно получен",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = User.class, type = "array")
        )
    )
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(
        summary = "Получить пользователя по ID",
        description = "Получить информацию о конкретном пользователе по его идентификатору"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Информация о пользователе успешно получена",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = User.class)
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Пользователь с указанным ID не найден"
    )
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(
            @Parameter(description = "UUID пользователя", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Создать нового пользователя",
        description = "Создать нового пользователя в системе"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Пользователь успешно создан",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = User.class)
        )
    )
    @ApiResponse(
        responseCode = "400",
        description = "Неверные данные пользователя"
    )
    @PostMapping
    public ResponseEntity<User> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Данные пользователя для создания",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = User.class)
                )
            )
            @Valid @RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
        summary = "Обновить пользователя",
        description = "Обновить информацию о пользователе по его идентификатору"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Пользователь успешно обновлен",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = User.class)
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Пользователь с указанным ID не найден"
    )
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @Parameter(description = "UUID пользователя", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Обновленные данные пользователя",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = User.class)
                )
            )
            @Valid @RequestBody User user) {
        // Проверяем, что ID в пути совпадает с ID в теле запроса
        if (user.getId() != null && !user.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }
        
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(
        summary = "Удалить пользователя",
        description = "Удалить пользователя по его идентификатору"
    )
    @ApiResponse(
        responseCode = "204",
        description = "Пользователь успешно удален"
    )
    @ApiResponse(
        responseCode = "404",
        description = "Пользователь с указанным ID не найден"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "UUID пользователя", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}