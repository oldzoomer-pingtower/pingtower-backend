package ru.oldzoomer.pingtower.settings_manager.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.oldzoomer.pingtower.settings_manager.entity.UserRoleEntity;
import ru.oldzoomer.pingtower.settings_manager.repository.UserRoleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public UserRoleService(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @Cacheable(value = "userRoles", key = "#userId")
    public List<UUID> getRolesForUser(UUID userId) {
        return userRoleRepository.findByUserId(userId).stream()
                .map(UserRoleEntity::getRoleId)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "userRoles", key = "#roleId")
    public List<UUID> getUsersWithRole(UUID roleId) {
        return userRoleRepository.findByRoleId(roleId).stream()
                .map(UserRoleEntity::getUserId)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "userRoles", key = "#userId")
    @Transactional
    public void assignRoleToUser(UUID userId, UUID roleId) {
        // Проверим, что такой связи еще нет
        List<UserRoleEntity> existingRelations = userRoleRepository.findByUserId(userId);
        boolean alreadyAssigned = existingRelations.stream()
                .anyMatch(relation -> relation.getRoleId().equals(roleId));
        
        if (!alreadyAssigned) {
            UserRoleEntity userRoleEntity = new UserRoleEntity();
            userRoleEntity.setUserId(userId);
            userRoleEntity.setRoleId(roleId);
            userRoleEntity.setCreatedAt(LocalDateTime.now());
            userRoleRepository.save(userRoleEntity);
        }
    }

    @CacheEvict(value = "userRoles", key = "#userId")
    @Transactional
    public void removeRoleFromUser(UUID userId, UUID roleId) {
        userRoleRepository.deleteByUserIdAndRoleId(userId, roleId);
    }

    @CacheEvict(value = "userRoles", key = "#userId")
    @Transactional
    public void removeAllRolesFromUser(UUID userId) {
        List<UserRoleEntity> userRoles = userRoleRepository.findByUserId(userId);
        userRoleRepository.deleteAll(userRoles);
    }
}