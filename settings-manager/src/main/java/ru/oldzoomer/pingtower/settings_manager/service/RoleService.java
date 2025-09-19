package ru.oldzoomer.pingtower.settings_manager.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.oldzoomer.pingtower.settings_manager.dto.Role;
import ru.oldzoomer.pingtower.settings_manager.entity.RoleEntity;
import ru.oldzoomer.pingtower.settings_manager.repository.RoleRepository;
import ru.oldzoomer.pingtower.settings_manager.repository.UserRoleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public RoleService(RoleRepository roleRepository, UserRoleRepository userRoleRepository) {
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<Role> getRoleById(UUID id) {
        return roleRepository.findById(id).map(this::convertToDto);
    }

    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name).map(this::convertToDto);
    }

    @Transactional
    public Role createRole(Role role) {
        // Проверим, что роль с таким именем еще нет
        if (roleRepository.existsByName(role.getName())) {
            throw new RuntimeException("Role with name " + role.getName() + " already exists");
        }

        RoleEntity entity = convertToEntity(role);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        RoleEntity savedEntity = roleRepository.save(entity);
        return convertToDto(savedEntity);
    }

    @Transactional
    public Role updateRole(UUID id, Role role) {
        Optional<RoleEntity> existingEntity = roleRepository.findById(id);
        if (existingEntity.isPresent()) {
            RoleEntity entity = existingEntity.get();
            
            // Проверим, что нового имени роли нет у других ролей
            if (!entity.getName().equals(role.getName()) && 
                roleRepository.existsByName(role.getName())) {
                throw new RuntimeException("Role with name " + role.getName() + " already exists");
            }

            entity.setName(role.getName());
            entity.setDescription(role.getDescription());
            entity.setPermissions(role.getPermissions());
            entity.setUpdatedAt(LocalDateTime.now());

            RoleEntity updatedEntity = roleRepository.save(entity);
            return convertToDto(updatedEntity);
        } else {
            throw new RuntimeException("Role not found with id: " + id);
        }
    }

    @Transactional
    public void deleteRole(UUID id) {
        if (roleRepository.existsById(id)) {
            // Удалим все связи пользователь-роль для этой роли
            userRoleRepository.findByRoleId(id).forEach(userRoleRepository::delete);
            
            // Удалим саму роль
            roleRepository.deleteById(id);
        } else {
            throw new RuntimeException("Role not found with id: " + id);
        }
    }

    private Role convertToDto(RoleEntity entity) {
        Role dto = new Role();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPermissions(entity.getPermissions());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    private RoleEntity convertToEntity(Role dto) {
        RoleEntity entity = new RoleEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPermissions(dto.getPermissions());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }
}