package ru.oldzoomer.pingtower.settings_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.oldzoomer.pingtower.settings_manager.entity.UserRoleEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UUID> {
    List<UserRoleEntity> findByUserId(UUID userId);
    List<UserRoleEntity> findByRoleId(UUID roleId);
    void deleteByUserIdAndRoleId(UUID userId, UUID roleId);
}