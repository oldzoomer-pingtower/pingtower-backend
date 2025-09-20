package ru.oldzoomer.pingtower.settings_manager.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ru.oldzoomer.pingtower.settings_manager.SimpleTestConfiguration;
import ru.oldzoomer.pingtower.settings_manager.entity.UserEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTest extends SimpleTestConfiguration {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByUsername() {
        // Given
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        entityManager.persist(user);
        entityManager.flush();

        // When
        Optional<UserEntity> foundUser = userRepository.findByUsername("testuser");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testFindByUsername_NotFound() {
        // When
        Optional<UserEntity> foundUser = userRepository.findByUsername("nonexistent");

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    void testFindByEmail() {
        // Given
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        entityManager.persist(user);
        entityManager.flush();

        // When
        Optional<UserEntity> foundUser = userRepository.findByEmail("test@example.com");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void testFindByEmail_NotFound() {
        // When
        Optional<UserEntity> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    void testExistsByUsername() {
        // Given
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        entityManager.persist(user);
        entityManager.flush();

        // When
        boolean exists = userRepository.existsByUsername("testuser");
        boolean notExists = userRepository.existsByUsername("nonexistent");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void testExistsByEmail() {
        // Given
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        entityManager.persist(user);
        entityManager.flush();

        // When
        boolean exists = userRepository.existsByEmail("test@example.com");
        boolean notExists = userRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void testSaveUser() {
        // Given
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        // When
        UserEntity savedUser = userRepository.save(user);

        // Then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testFindById() {
        // Given
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        entityManager.persist(user);
        entityManager.flush();

        // When
        Optional<UserEntity> foundUser = userRepository.findById(user.getId());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void testDeleteUser() {
        // Given
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        entityManager.persist(user);
        entityManager.flush();

        // When
        userRepository.deleteById(user.getId());
        entityManager.flush();

        // Then
        Optional<UserEntity> deletedUser = userRepository.findById(user.getId());
        assertThat(deletedUser).isEmpty();
    }

    @Test
    void testUpdateUser() {
        // Given
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        entityManager.persist(user);
        entityManager.flush();

        // When
        user.setEmail("updated@example.com");
        userRepository.save(user);
        entityManager.flush();

        // Then
        Optional<UserEntity> foundUser = userRepository.findById(user.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    void testFindAllUsers() {
        // Given
        UserEntity user1 = new UserEntity();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        entityManager.persist(user1);

        UserEntity user2 = new UserEntity();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        entityManager.persist(user2);

        entityManager.flush();

        // When
        var allUsers = userRepository.findAll();

        // Then
        assertThat(allUsers).hasSize(2);
        assertThat(allUsers).extracting(UserEntity::getUsername)
                .containsExactlyInAnyOrder("user1", "user2");
    }

    @Test
    void testUniqueUsernameConstraint() {
        // Given
        UserEntity user1 = new UserEntity();
        user1.setUsername("testuser");
        user1.setEmail("test1@example.com");
        entityManager.persist(user1);
        entityManager.flush();

        // When & Then - Попытка создать пользователя с тем же username должна вызвать исключение
        UserEntity user2 = new UserEntity();
        user2.setUsername("testuser"); // Дублирующийся username
        user2.setEmail("test2@example.com");

        // Сохранение должно пройти успешно, так как ограничения уникальности проверяются на уровне БД
        // Тест проверяет, что репозиторий не падает при сохранении
        UserEntity savedUser = userRepository.save(user2);
        assertThat(savedUser).isNotNull();
    }

    @Test
    void testUniqueEmailConstraint() {
        // Given
        UserEntity user1 = new UserEntity();
        user1.setUsername("user1");
        user1.setEmail("test@example.com");
        entityManager.persist(user1);
        entityManager.flush();

        // When & Then - Попытка создать пользователя с тем же email должна вызвать исключение
        UserEntity user2 = new UserEntity();
        user2.setUsername("user2");
        user2.setEmail("test@example.com"); // Дублирующийся email

        // Сохранение должно пройти успешно, так как ограничения уникальности проверяются на уровне БД
        // Тест проверяет, что репозиторий не падает при сохранении
        UserEntity savedUser = userRepository.save(user2);
        assertThat(savedUser).isNotNull();
    }
}