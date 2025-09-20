package ru.oldzoomer.pingtower.settings_manager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.oldzoomer.pingtower.settings_manager.entity.UserRoleEntity;
import ru.oldzoomer.pingtower.settings_manager.exception.EntityNotFoundException;
import ru.oldzoomer.pingtower.settings_manager.repository.UserRoleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRoleServiceTest {

    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private UserRoleService userRoleService;

    private UUID testUserId;
    private UUID testRoleId;
    private UserRoleEntity testUserRoleEntity;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testRoleId = UUID.randomUUID();
        
        testUserRoleEntity = new UserRoleEntity();
        testUserRoleEntity.setUserId(testUserId);
        testUserRoleEntity.setRoleId(testRoleId);
        testUserRoleEntity.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testGetRolesForUser() {
        when(userRoleRepository.findByUserId(testUserId)).thenReturn(List.of(testUserRoleEntity));

        List<UUID> result = userRoleService.getRolesForUser(testUserId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testRoleId, result.get(0));
        verify(userRoleRepository).findByUserId(testUserId);
    }

    @Test
    void testGetRolesForUser_Empty() {
        when(userRoleRepository.findByUserId(testUserId)).thenReturn(List.of());

        assertThrows(EntityNotFoundException.class, () -> {
            userRoleService.getRolesForUser(testUserId);
        });
        verify(userRoleRepository).findByUserId(testUserId);
    }

    @Test
    void testGetUsersWithRole() {
        when(userRoleRepository.findByRoleId(testRoleId)).thenReturn(List.of(testUserRoleEntity));

        List<UUID> result = userRoleService.getUsersWithRole(testRoleId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUserId, result.get(0));
        verify(userRoleRepository).findByRoleId(testRoleId);
    }

    @Test
    void testGetUsersWithRole_Empty() {
        when(userRoleRepository.findByRoleId(testRoleId)).thenReturn(List.of());

        assertThrows(EntityNotFoundException.class, () -> {
            userRoleService.getUsersWithRole(testRoleId);
        });
        verify(userRoleRepository).findByRoleId(testRoleId);
    }

    @Test
    void testAssignRoleToUser_NewAssignment() {
        when(userRoleRepository.findByUserId(testUserId)).thenReturn(List.of());
        when(userRoleRepository.save(any(UserRoleEntity.class))).thenReturn(testUserRoleEntity);

        userRoleService.assignRoleToUser(testUserId, testRoleId);

        verify(userRoleRepository).findByUserId(testUserId);
        verify(userRoleRepository).save(any(UserRoleEntity.class));
    }

    @Test
    void testAssignRoleToUser_AlreadyAssigned() {
        when(userRoleRepository.findByUserId(testUserId)).thenReturn(List.of(testUserRoleEntity));

        userRoleService.assignRoleToUser(testUserId, testRoleId);

        verify(userRoleRepository).findByUserId(testUserId);
        verify(userRoleRepository, never()).save(any(UserRoleEntity.class));
    }

    @Test
    void testAssignRoleToUser_MultipleRoles() {
        UUID anotherRoleId = UUID.randomUUID();
        UserRoleEntity anotherUserRoleEntity = new UserRoleEntity();
        anotherUserRoleEntity.setUserId(testUserId);
        anotherUserRoleEntity.setRoleId(anotherRoleId);
        anotherUserRoleEntity.setCreatedAt(LocalDateTime.now());

        when(userRoleRepository.findByUserId(testUserId)).thenReturn(List.of(testUserRoleEntity));
        when(userRoleRepository.save(any(UserRoleEntity.class))).thenReturn(anotherUserRoleEntity);

        userRoleService.assignRoleToUser(testUserId, anotherRoleId);

        verify(userRoleRepository).findByUserId(testUserId);
        verify(userRoleRepository).save(any(UserRoleEntity.class));
    }

    @Test
    void testRemoveRoleFromUser() {
        when(userRoleRepository.findByUserIdAndRoleId(testUserId, testRoleId)).thenReturn(List.of(testUserRoleEntity));

        assertDoesNotThrow(() -> {
            userRoleService.removeRoleFromUser(testUserId, testRoleId);
        });

        verify(userRoleRepository).findByUserIdAndRoleId(testUserId, testRoleId);
        verify(userRoleRepository).deleteByUserIdAndRoleId(testUserId, testRoleId);
    }

    @Test
    void testRemoveRoleFromUser_NotFound() {
        when(userRoleRepository.findByUserIdAndRoleId(testUserId, testRoleId)).thenReturn(List.of());

        assertThrows(EntityNotFoundException.class, () -> {
            userRoleService.removeRoleFromUser(testUserId, testRoleId);
        });

        verify(userRoleRepository).findByUserIdAndRoleId(testUserId, testRoleId);
        verify(userRoleRepository, never()).deleteByUserIdAndRoleId(any(), any());
    }

    @Test
    void testRemoveAllRolesFromUser() {
        when(userRoleRepository.findByUserId(testUserId)).thenReturn(List.of(testUserRoleEntity));

        userRoleService.removeAllRolesFromUser(testUserId);

        verify(userRoleRepository).findByUserId(testUserId);
        verify(userRoleRepository).deleteAll(List.of(testUserRoleEntity));
    }

    @Test
    void testRemoveAllRolesFromUser_Empty() {
        when(userRoleRepository.findByUserId(testUserId)).thenReturn(List.of());

        assertThrows(EntityNotFoundException.class, () -> {
            userRoleService.removeAllRolesFromUser(testUserId);
        });

        verify(userRoleRepository).findByUserId(testUserId);
        verify(userRoleRepository, never()).deleteAll(any());
    }

    @Test
    void testAssignRoleToUser_MultipleExistingRoles() {
        UUID existingRoleId1 = UUID.randomUUID();
        UUID existingRoleId2 = UUID.randomUUID();
        UUID newRoleId = UUID.randomUUID();

        UserRoleEntity existingRole1 = new UserRoleEntity();
        existingRole1.setUserId(testUserId);
        existingRole1.setRoleId(existingRoleId1);

        UserRoleEntity existingRole2 = new UserRoleEntity();
        existingRole2.setUserId(testUserId);
        existingRole2.setRoleId(existingRoleId2);

        when(userRoleRepository.findByUserId(testUserId)).thenReturn(List.of(existingRole1, existingRole2));
        when(userRoleRepository.save(any(UserRoleEntity.class))).thenReturn(testUserRoleEntity);

        userRoleService.assignRoleToUser(testUserId, newRoleId);

        verify(userRoleRepository).findByUserId(testUserId);
        verify(userRoleRepository).save(any(UserRoleEntity.class));
    }

    @Test
    void testGetRolesForUser_MultipleRoles() {
        UUID roleId2 = UUID.randomUUID();
        UserRoleEntity userRoleEntity2 = new UserRoleEntity();
        userRoleEntity2.setUserId(testUserId);
        userRoleEntity2.setRoleId(roleId2);

        when(userRoleRepository.findByUserId(testUserId)).thenReturn(List.of(testUserRoleEntity, userRoleEntity2));

        List<UUID> result = userRoleService.getRolesForUser(testUserId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(testRoleId));
        assertTrue(result.contains(roleId2));
        verify(userRoleRepository).findByUserId(testUserId);
    }

    @Test
    void testGetUsersWithRole_MultipleUsers() {
        UUID userId2 = UUID.randomUUID();
        UserRoleEntity userRoleEntity2 = new UserRoleEntity();
        userRoleEntity2.setUserId(userId2);
        userRoleEntity2.setRoleId(testRoleId);

        when(userRoleRepository.findByRoleId(testRoleId)).thenReturn(List.of(testUserRoleEntity, userRoleEntity2));

        List<UUID> result = userRoleService.getUsersWithRole(testRoleId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(testUserId));
        assertTrue(result.contains(userId2));
        verify(userRoleRepository).findByRoleId(testRoleId);
    }

    @Test
    void testRemoveAllRolesFromUser_MultipleRoles() {
        UUID roleId2 = UUID.randomUUID();
        UserRoleEntity userRoleEntity2 = new UserRoleEntity();
        userRoleEntity2.setUserId(testUserId);
        userRoleEntity2.setRoleId(roleId2);

        when(userRoleRepository.findByUserId(testUserId)).thenReturn(List.of(testUserRoleEntity, userRoleEntity2));

        userRoleService.removeAllRolesFromUser(testUserId);

        verify(userRoleRepository).findByUserId(testUserId);
        verify(userRoleRepository).deleteAll(List.of(testUserRoleEntity, userRoleEntity2));
    }
}