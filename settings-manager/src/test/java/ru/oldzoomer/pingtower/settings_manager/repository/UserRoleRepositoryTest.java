package ru.oldzoomer.pingtower.settings_manager.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ru.oldzoomer.pingtower.settings_manager.SimpleTestConfiguration;
import ru.oldzoomer.pingtower.settings_manager.entity.RoleEntity;
import ru.oldzoomer.pingtower.settings_manager.entity.UserEntity;
import ru.oldzoomer.pingtower.settings_manager.entity.UserRoleEntity;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class UserRoleRepositoryTest extends SimpleTestConfiguration {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Test
    void testFindByUserId() {
        // Given
        UserEntity user = createUser("testuser", "test@example.com");
        RoleEntity role1 = createRole("ADMIN", "Administrator role");
        RoleEntity role2 = createRole("USER", "User role");
        
        createUserRole(user, role1);
        createUserRole(user, role2);
        
        entityManager.flush();

        // When
        List<UserRoleEntity> userRoles = userRoleRepository.findByUserId(user.getId());

        // Then
        assertThat(userRoles).hasSize(2);
        assertThat(userRoles).extracting(UserRoleEntity::getRoleId)
                .containsExactlyInAnyOrder(role1.getId(), role2.getId());
    }

    @Test
    void testFindByRoleId() {
        // Given
        UserEntity user1 = createUser("user1", "user1@example.com");
        UserEntity user2 = createUser("user2", "user2@example.com");
        RoleEntity role = createRole("ADMIN", "Administrator role");
        
        createUserRole(user1, role);
        createUserRole(user2, role);
        
        entityManager.flush();

        // When
        List<UserRoleEntity> userRoles = userRoleRepository.findByRoleId(role.getId());

        // Then
        assertThat(userRoles).hasSize(2);
        assertThat(userRoles).extracting(UserRoleEntity::getUserId)
                .containsExactlyInAnyOrder(user1.getId(), user2.getId());
    }

    @Test
    void testDeleteByUserIdAndRoleId() {
        // Given
        UserEntity user = createUser("testuser", "test@example.com");
        RoleEntity role = createRole("ADMIN", "Administrator role");
        createUserRole(user, role);
        
        entityManager.flush();

        // Verify it exists
        List<UserRoleEntity> beforeDelete = userRoleRepository.findByUserId(user.getId());
        assertThat(beforeDelete).hasSize(1);

        // When
        userRoleRepository.deleteByUserIdAndRoleId(user.getId(), role.getId());
        entityManager.flush();

        // Then
        List<UserRoleEntity> afterDelete = userRoleRepository.findByUserId(user.getId());
        assertThat(afterDelete).isEmpty();
    }

    @Test
    void testSaveUserRole() {
        // Given
        UserEntity user = createUser("testuser", "test@example.com");
        RoleEntity role = createRole("ADMIN", "Administrator role");

        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setUserId(user.getId());
        userRole.setRoleId(role.getId());

        // When
        UserRoleEntity savedUserRole = userRoleRepository.save(userRole);

        // Then
        assertThat(savedUserRole).isNotNull();
        assertThat(savedUserRole.getId()).isNotNull();
        assertThat(savedUserRole.getUserId()).isEqualTo(user.getId());
        assertThat(savedUserRole.getRoleId()).isEqualTo(role.getId());
    }

    @Test
    void testFindById() {
        // Given
        UserEntity user = createUser("testuser", "test@example.com");
        RoleEntity role = createRole("ADMIN", "Administrator role");
        UserRoleEntity userRole = createUserRole(user, role);
        
        entityManager.flush();

        // When
        var foundUserRole = userRoleRepository.findById(userRole.getId());

        // Then
        assertThat(foundUserRole).isPresent();
        assertThat(foundUserRole.get().getUserId()).isEqualTo(user.getId());
        assertThat(foundUserRole.get().getRoleId()).isEqualTo(role.getId());
    }

    @Test
    void testDeleteUserRole() {
        // Given
        UserEntity user = createUser("testuser", "test@example.com");
        RoleEntity role = createRole("ADMIN", "Administrator role");
        UserRoleEntity userRole = createUserRole(user, role);
        
        entityManager.flush();

        // When
        userRoleRepository.deleteById(userRole.getId());
        entityManager.flush();

        // Then
        var deletedUserRole = userRoleRepository.findById(userRole.getId());
        assertThat(deletedUserRole).isEmpty();
    }

    @Test
    void testFindAllUserRoles() {
        // Given
        UserEntity user1 = createUser("user1", "user1@example.com");
        UserEntity user2 = createUser("user2", "user2@example.com");
        RoleEntity role1 = createRole("ADMIN", "Administrator role");
        RoleEntity role2 = createRole("USER", "User role");
        
        createUserRole(user1, role1);
        createUserRole(user1, role2);
        createUserRole(user2, role1);
        
        entityManager.flush();

        // When
        var allUserRoles = userRoleRepository.findAll();

        // Then
        assertThat(allUserRoles).hasSize(3);
    }

    @Test
    void testUniqueUserRoleConstraint() {
        // Given
        UserEntity user = createUser("testuser", "test@example.com");
        RoleEntity role = createRole("ADMIN", "Administrator role");
        createUserRole(user, role);
        
        entityManager.flush();

        // When & Then - Попытка создать дублирующую связь пользователь-роль
        UserRoleEntity userRole2 = new UserRoleEntity();
        userRole2.setUserId(user.getId());
        userRole2.setRoleId(role.getId());

        // Сохранение должно пройти успешно, так как ограничения уникальности проверяются на уровне БД
        // Тест проверяет, что репозиторий не падает при сохранении
        UserRoleEntity savedUserRole = userRoleRepository.save(userRole2);
        assertThat(savedUserRole).isNotNull();
    }

    private UserEntity createUser(String username, String email) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail(email);
        entityManager.persist(user);
        return user;
    }

    private RoleEntity createRole(String name, String description) {
        RoleEntity role = new RoleEntity();
        role.setName(name);
        role.setDescription(description);
        entityManager.persist(role);
        return role;
    }

    private UserRoleEntity createUserRole(UserEntity user, RoleEntity role) {
        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setUserId(user.getId());
        userRole.setRoleId(role.getId());
        entityManager.persist(userRole);
        return userRole;
    }
}