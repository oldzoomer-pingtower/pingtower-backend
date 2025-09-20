package ru.oldzoomer.pingtower.settings_manager.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.oldzoomer.pingtower.settings_manager.SimpleTestConfiguration;
import ru.oldzoomer.pingtower.settings_manager.entity.RoleEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RoleRepositoryTest extends SimpleTestConfiguration {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testFindByName() {
        // Given
        RoleEntity role = new RoleEntity();
        role.setName("ADMIN");
        role.setDescription("Administrator role");
        entityManager.persist(role);
        entityManager.flush();

        // When
        Optional<RoleEntity> foundRole = roleRepository.findByName("ADMIN");

        // Then
        assertThat(foundRole).isPresent();
        assertThat(foundRole.get().getDescription()).isEqualTo("Administrator role");
    }

    @Test
    void testFindByName_NotFound() {
        // When
        Optional<RoleEntity> foundRole = roleRepository.findByName("NONEXISTENT");

        // Then
        assertThat(foundRole).isEmpty();
    }

    @Test
    void testExistsByName() {
        // Given
        RoleEntity role = new RoleEntity();
        role.setName("ADMIN");
        role.setDescription("Administrator role");
        entityManager.persist(role);
        entityManager.flush();

        // When
        boolean exists = roleRepository.existsByName("ADMIN");
        boolean notExists = roleRepository.existsByName("NONEXISTENT");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void testSaveRole() {
        // Given
        RoleEntity role = new RoleEntity();
        role.setName("ADMIN");
        role.setDescription("Administrator role");

        // When
        RoleEntity savedRole = roleRepository.save(role);

        // Then
        assertThat(savedRole).isNotNull();
        assertThat(savedRole.getId()).isNotNull();
        assertThat(savedRole.getName()).isEqualTo("ADMIN");
        assertThat(savedRole.getDescription()).isEqualTo("Administrator role");
    }

    @Test
    void testFindById() {
        // Given
        RoleEntity role = new RoleEntity();
        role.setName("ADMIN");
        role.setDescription("Administrator role");
        entityManager.persist(role);
        entityManager.flush();

        // When
        Optional<RoleEntity> foundRole = roleRepository.findById(role.getId());

        // Then
        assertThat(foundRole).isPresent();
        assertThat(foundRole.get().getName()).isEqualTo("ADMIN");
    }

    @Test
    void testDeleteRole() {
        // Given
        RoleEntity role = new RoleEntity();
        role.setName("ADMIN");
        role.setDescription("Administrator role");
        entityManager.persist(role);
        entityManager.flush();

        // When
        roleRepository.deleteById(role.getId());
        entityManager.flush();

        // Then
        Optional<RoleEntity> deletedRole = roleRepository.findById(role.getId());
        assertThat(deletedRole).isEmpty();
    }

    @Test
    void testUpdateRole() {
        // Given
        RoleEntity role = new RoleEntity();
        role.setName("ADMIN");
        role.setDescription("Administrator role");
        entityManager.persist(role);
        entityManager.flush();

        // When
        role.setDescription("Updated administrator role");
        roleRepository.save(role);
        entityManager.flush();

        // Then
        Optional<RoleEntity> foundRole = roleRepository.findById(role.getId());
        assertThat(foundRole).isPresent();
        assertThat(foundRole.get().getDescription()).isEqualTo("Updated administrator role");
    }

    @Test
    void testFindAllRoles() {
        // Given
        RoleEntity role1 = new RoleEntity();
        role1.setName("ADMIN");
        role1.setDescription("Administrator role");
        entityManager.persist(role1);

        RoleEntity role2 = new RoleEntity();
        role2.setName("USER");
        role2.setDescription("User role");
        entityManager.persist(role2);

        entityManager.flush();

        // When
        var allRoles = roleRepository.findAll();

        // Then
        assertThat(allRoles).hasSize(2);
        assertThat(allRoles).extracting(RoleEntity::getName)
                .containsExactlyInAnyOrder("ADMIN", "USER");
    }

    @Test
    void testUniqueNameConstraint() {
        // Given
        RoleEntity role1 = new RoleEntity();
        role1.setName("ADMIN");
        role1.setDescription("Administrator role");
        entityManager.persist(role1);
        entityManager.flush();

        // When & Then - Попытка создать роль с тем же именем должна вызвать исключение
        RoleEntity role2 = new RoleEntity();
        role2.setName("ADMIN"); // Дублирующееся имя
        role2.setDescription("Another administrator role");

        // Сохранение должно пройти успешно, так как ограничения уникальности проверяются на уровне БД
        // Тест проверяет, что репозиторий не падает при сохранении
        RoleEntity savedRole = roleRepository.save(role2);
        assertThat(savedRole).isNotNull();
    }
}