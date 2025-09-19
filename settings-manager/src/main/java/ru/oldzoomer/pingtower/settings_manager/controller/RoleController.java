package ru.oldzoomer.pingtower.settings_manager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.oldzoomer.pingtower.settings_manager.dto.Role;
import ru.oldzoomer.pingtower.settings_manager.service.RoleService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> getRole(@PathVariable UUID id) {
        Optional<Role> role = roleService.getRoleById(id);
        return role.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        try {
            Role createdRole = roleService.createRole(role);
            return ResponseEntity.ok(createdRole);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable UUID id, @RequestBody Role role) {
        try {
            Role updatedRole = roleService.updateRole(id, role);
            return ResponseEntity.ok(updatedRole);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        try {
            roleService.deleteRole(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}