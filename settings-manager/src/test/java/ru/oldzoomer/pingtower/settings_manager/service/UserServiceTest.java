package ru.oldzoomer.pingtower.settings_manager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.oldzoomer.pingtower.settings_manager.dto.User;
import ru.oldzoomer.pingtower.settings_manager.entity.UserEntity;
import ru.oldzoomer.pingtower.settings_manager.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserEntity testEntity;

    @BeforeEach
    void setUp() {
        UUID testId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        testUser = new User();
        testUser.setId(testId);
        testUser.setUsername("john_doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setCreatedAt(now);
        testUser.setUpdatedAt(now);

        testEntity = new UserEntity();
        testEntity.setId(testId);
        testEntity.setUsername("john_doe");
        testEntity.setEmail("john.doe@example.com");
        testEntity.setFirstName("John");
        testEntity.setLastName("Doe");
        testEntity.setCreatedAt(now);
        testEntity.setUpdatedAt(now);
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(testEntity));

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("john_doe", result.getFirst().getUsername());
        verify(userRepository).findAll();
    }

    @Test
    void testGetAllUsers_Empty() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findAll();
    }

    @Test
    void testGetUserById_Exists() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testEntity));

        Optional<User> result = userService.getUserById(testUser.getId());

        assertTrue(result.isPresent());
        assertEquals("john_doe", result.get().getUsername());
        assertEquals("john.doe@example.com", result.get().getEmail());
        verify(userRepository).findById(testUser.getId());
    }

    @Test
    void testGetUserById_NotExists() {
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(nonExistentId);

        assertFalse(result.isPresent());
        verify(userRepository).findById(nonExistentId);
    }

    @Test
    void testGetUserByUsername_Exists() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(testEntity));

        Optional<User> result = userService.getUserByUsername("john_doe");

        assertTrue(result.isPresent());
        assertEquals("john_doe", result.get().getUsername());
        verify(userRepository).findByUsername("john_doe");
    }

    @Test
    void testGetUserByUsername_NotExists() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserByUsername("nonexistent");

        assertFalse(result.isPresent());
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    void testGetUserByEmail_Exists() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testEntity));

        Optional<User> result = userService.getUserByEmail("john.doe@example.com");

        assertTrue(result.isPresent());
        assertEquals("john.doe@example.com", result.get().getEmail());
        verify(userRepository).findByEmail("john.doe@example.com");
    }

    @Test
    void testGetUserByEmail_NotExists() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserByEmail("nonexistent@example.com");

        assertFalse(result.isPresent());
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void testCreateUser_Success() {
        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(testEntity);

        User result = userService.createUser(testUser);

        assertNotNull(result);
        assertEquals("john_doe", result.getUsername());
        assertEquals("john.doe@example.com", result.getEmail());
        verify(userRepository).existsByUsername("john_doe");
        verify(userRepository).existsByEmail("john.doe@example.com");
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void testCreateUser_UsernameExists() {
        when(userRepository.existsByUsername("john_doe")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> userService.createUser(testUser));

        verify(userRepository).existsByUsername("john_doe");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testCreateUser_EmailExists() {
        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> userService.createUser(testUser));

        verify(userRepository).existsByUsername("john_doe");
        verify(userRepository).existsByEmail("john.doe@example.com");
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testUpdateUser_Success() {
        User updatedUser = new User();
        updatedUser.setUsername("john_doe_updated");
        updatedUser.setEmail("john.updated@example.com");
        updatedUser.setFirstName("John Updated");
        updatedUser.setLastName("Doe Updated");

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testEntity));
        when(userRepository.existsByUsername("john_doe_updated")).thenReturn(false);
        when(userRepository.existsByEmail("john.updated@example.com")).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(testEntity);

        User result = userService.updateUser(testUser.getId(), updatedUser);

        assertNotNull(result);
        verify(userRepository).findById(testUser.getId());
        verify(userRepository).existsByUsername("john_doe_updated");
        verify(userRepository).existsByEmail("john.updated@example.com");
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void testUpdateUser_NotExists() {
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.updateUser(nonExistentId, testUser));

        verify(userRepository).findById(nonExistentId);
        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testUpdateUser_UsernameExists() {
        User updatedUser = new User();
        updatedUser.setUsername("existing_user");
        updatedUser.setEmail("john.doe@example.com");

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testEntity));
        when(userRepository.existsByUsername("existing_user")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> userService.updateUser(testUser.getId(), updatedUser));

        verify(userRepository).findById(testUser.getId());
        verify(userRepository).existsByUsername("existing_user");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testUpdateUser_EmailExists() {
        User updatedUser = new User();
        updatedUser.setUsername("john_doe");
        updatedUser.setEmail("existing@example.com");

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testEntity));
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> userService.updateUser(testUser.getId(), updatedUser));

        verify(userRepository).findById(testUser.getId());
        verify(userRepository, never()).existsByUsername("john_doe");
        verify(userRepository).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testUpdateUser_SameUsernameAndEmail() {
        User updatedUser = new User();
        updatedUser.setUsername("john_doe"); // Same as existing
        updatedUser.setEmail("john.doe@example.com"); // Same as existing
        updatedUser.setFirstName("John Updated");
        updatedUser.setLastName("Doe Updated");

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(testEntity);

        User result = userService.updateUser(testUser.getId(), updatedUser);

        assertNotNull(result);
        verify(userRepository).findById(testUser.getId());
        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.existsById(testUser.getId())).thenReturn(true);

        assertDoesNotThrow(() -> userService.deleteUser(testUser.getId()));

        verify(userRepository).existsById(testUser.getId());
        verify(userRepository).deleteById(testUser.getId());
    }

    @Test
    void testDeleteUser_NotExists() {
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.existsById(nonExistentId)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> userService.deleteUser(nonExistentId));

        verify(userRepository).existsById(nonExistentId);
        verify(userRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void testCreateUser_NullValues() {
        User userWithNulls = new User();
        userWithNulls.setUsername("test_user");
        userWithNulls.setEmail("test@example.com");

        when(userRepository.existsByUsername("test_user")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(testEntity);

        User result = userService.createUser(userWithNulls);

        assertNotNull(result);
        verify(userRepository).existsByUsername("test_user");
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository).save(any(UserEntity.class));
    }
}