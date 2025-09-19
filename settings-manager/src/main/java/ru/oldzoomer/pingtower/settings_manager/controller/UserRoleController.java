package ru.oldzoomer.pingtower.settings_manager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.oldzoomer.pingtower.settings_manager.service.UserRoleService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user-roles")
public class UserRoleController {

    private final UserRoleService userRoleService;

    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UUID>> getRolesForUser(@PathVariable UUID userId) {
        List<UUID> roles = userRoleService.getRolesForUser(userId);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/role/{roleId}")
    public ResponseEntity<List<UUID>> getUsersWithRole(@PathVariable UUID roleId) {
        List<UUID> users = userRoleService.getUsersWithRole(roleId);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/{userId}/{roleId}")
    public ResponseEntity<Void> assignRoleToUser(@PathVariable UUID userId, @PathVariable UUID roleId) {
        userRoleService.assignRoleToUser(userId, roleId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/{roleId}")
    public ResponseEntity<Void> removeRoleFromUser(@PathVariable UUID userId, @PathVariable UUID roleId) {
        userRoleService.removeRoleFromUser(userId, roleId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> removeAllRolesFromUser(@PathVariable UUID userId) {
        userRoleService.removeAllRolesFromUser(userId);
        return ResponseEntity.noContent().build();
    }
}