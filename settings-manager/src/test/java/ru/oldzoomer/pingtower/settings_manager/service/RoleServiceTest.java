package ru.oldzoomer.pingtower.settings_manager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.oldzoomer.pingtower.settings_manager.dto.Role;
import ru.oldzoomer.pingtower.settings_manager.entity.RoleEntity;
import ru.oldzoomer.pingtower.settings_manager.repository.RoleRepository;
import ru.oldzoomer.pingtower.settings_manager.repository.UserRoleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private RoleService roleService;

    private Role testRole;
    private RoleEntity testEntity;

    @BeforeEach
    void setUp() {
        UUID testId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        testRole = new Role();
        testRole.setId(testId);
        testRole.setName("ADMIN");
        testRole.setDescription("Администратор системы");
        testRole.setPermissions(List.of("users.read", "users.write"));
        testRole.setCreatedAt(now);
        testRole.setUpdatedAt(now);

        testEntity = new RoleEntity();
        testEntity.setId(testId);
        testEntity.setName("ADMIN");
        testEntity.setDescription("Администратор системы");
        testEntity.setPermissions(List.of("users.read", "users.write"));
        testEntity.setCreatedAt(now);
        testEntity.setUpdatedAt(now);
    }

    @Test
    void testGetAllRoles() {
        when(roleRepository.findAll()).thenReturn(List.of(testEntity));

        List<Role> result = roleService.getAllRoles();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ADMIN", result.getFirst().getName());
        verify(roleRepository).findAll();
    }

    @Test
    void testGetAllRoles_Empty() {
        when(roleRepository.findAll()).thenReturn(List.of());

        List<Role> result = roleService.getAllRoles();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(roleRepository).findAll();
    }

    @Test
    void testGetRoleById_Exists() {
        when(roleRepository.findById(testRole.getId())).thenReturn(Optional.of(testEntity));

        Optional<Role> result = roleService.getRoleById(testRole.getId());

        assertTrue(result.isPresent());
        assertEquals("ADMIN", result.get().getName());
        assertEquals("Администратор системы", result.get().getDescription());
        verify(roleRepository).findById(testRole.getId());
    }

    @Test
    void testGetRoleById_NotExists() {
        UUID nonExistentId = UUID.randomUUID();
        when(roleRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        Optional<Role> result = roleService.getRoleById(nonExistentId);

        assertFalse(result.isPresent());
        verify(roleRepository).findById(nonExistentId);
    }

    @Test
    void testGetRoleByName_Exists() {
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(testEntity));

        Optional<Role> result = roleService.getRoleByName("ADMIN");

        assertTrue(result.isPresent());
        assertEquals("ADMIN", result.get().getName());
        verify(roleRepository).findByName("ADMIN");
    }

    @Test
    void testGetRoleByName_NotExists() {
        when(roleRepository.findByName("nonexistent")).thenReturn(Optional.empty());

        Optional<Role> result = roleService.getRoleByName("nonexistent");

        assertFalse(result.isPresent());
        verify(roleRepository).findByName("nonexistent");
    }

    @Test
    void testCreateRole_Success() {
        when(roleRepository.existsByName("ADMIN")).thenReturn(false);
        when(roleRepository.save(any(RoleEntity.class))).thenReturn(testEntity);

        Role result = roleService.createRole(testRole);

        assertNotNull(result);
        assertEquals("ADMIN", result.getName());
        assertEquals("Администратор системы", result.getDescription());
        verify(roleRepository).existsByName("ADMIN");
        verify(roleRepository).save(any(RoleEntity.class));
    }

    @Test
    void testCreateRole_NameExists() {
        when(roleRepository.existsByName("ADMIN")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> roleService.createRole(testRole));

        verify(roleRepository).existsByName("ADMIN");
        verify(roleRepository, never()).save(any(RoleEntity.class));
    }

    @Test
    void testUpdateRole_Success() {
        Role updatedRole = new Role();
        updatedRole.setName("ADMIN_UPDATED");
        updatedRole.setDescription("Обновленный администратор");
        updatedRole.setPermissions(List.of("users.read", "users.write", "users.delete"));

        when(roleRepository.findById(testRole.getId())).thenReturn(Optional.of(testEntity));
        when(roleRepository.existsByName("ADMIN_UPDATED")).thenReturn(false);
        when(roleRepository.save(any(RoleEntity.class))).thenReturn(testEntity);

        Role result = roleService.updateRole(testRole.getId(), updatedRole);

        assertNotNull(result);
        verify(roleRepository).findById(testRole.getId());
        verify(roleRepository).existsByName("ADMIN_UPDATED");
        verify(roleRepository).save(any(RoleEntity.class));
    }

    @Test
    void testUpdateRole_NotExists() {
        UUID nonExistentId = UUID.randomUUID();
        when(roleRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> roleService.updateRole(nonExistentId, testRole));

        verify(roleRepository).findById(nonExistentId);
        verify(roleRepository, never()).existsByName(anyString());
        verify(roleRepository, never()).save(any(RoleEntity.class));
    }

    @Test
    void testUpdateRole_NameExists() {
        Role updatedRole = new Role();
        updatedRole.setName("EXISTING_ROLE");

        when(roleRepository.findById(testRole.getId())).thenReturn(Optional.of(testEntity));
        when(roleRepository.existsByName("EXISTING_ROLE")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> roleService.updateRole(testRole.getId(), updatedRole));

        verify(roleRepository).findById(testRole.getId());
        verify(roleRepository).existsByName("EXISTING_ROLE");
        verify(roleRepository, never()).save(any(RoleEntity.class));
    }

    @Test
    void testUpdateRole_SameName() {
        Role updatedRole = new Role();
        updatedRole.setName("ADMIN"); // Same as existing
        updatedRole.setDescription("Обновленное описание");
        updatedRole.setPermissions(List.of("users.read"));

        when(roleRepository.findById(testRole.getId())).thenReturn(Optional.of(testEntity));
        when(roleRepository.save(any(RoleEntity.class))).thenReturn(testEntity);

        Role result = roleService.updateRole(testRole.getId(), updatedRole);

        assertNotNull(result);
        verify(roleRepository).findById(testRole.getId());
        verify(roleRepository, never()).existsByName(anyString());
        verify(roleRepository).save(any(RoleEntity.class));
    }

    @Test
    void testDeleteRole_Success() {
        when(roleRepository.existsById(testRole.getId())).thenReturn(true);
        when(userRoleRepository.findByRoleId(testRole.getId())).thenReturn(List.of());

        assertDoesNotThrow(() -> roleService.deleteRole(testRole.getId()));

        verify(roleRepository).existsById(testRole.getId());
        verify(userRoleRepository).findByRoleId(testRole.getId());
        verify(userRoleRepository, times(0)).delete(any());
        verify(roleRepository).deleteById(testRole.getId());
    }

    @Test
    void testDeleteRole_WithUserRoles() {
        when(roleRepository.existsById(testRole.getId())).thenReturn(true);
        when(userRoleRepository.findByRoleId(testRole.getId())).thenReturn(List.of(
            mock(ru.oldzoomer.pingtower.settings_manager.entity.UserRoleEntity.class),
            mock(ru.oldzoomer.pingtower.settings_manager.entity.UserRoleEntity.class)
        ));

        assertDoesNotThrow(() -> roleService.deleteRole(testRole.getId()));

        verify(roleRepository).existsById(testRole.getId());
        verify(userRoleRepository).findByRoleId(testRole.getId());
        verify(userRoleRepository, times(2)).delete(any());
        verify(roleRepository).deleteById(testRole.getId());
    }

    @Test
    void testDeleteRole_NotExists() {
        UUID nonExistentId = UUID.randomUUID();
        when(roleRepository.existsById(nonExistentId)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> roleService.deleteRole(nonExistentId));

        verify(roleRepository).existsById(nonExistentId);
        verify(userRoleRepository, never()).findByRoleId(any(UUID.class));
        verify(userRoleRepository, never()).delete(any());
        verify(roleRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void testCreateRole_NullValues() {
        Role roleWithNulls = new Role();
        roleWithNulls.setName("TEST_ROLE");

        when(roleRepository.existsByName("TEST_ROLE")).thenReturn(false);
        when(roleRepository.save(any(RoleEntity.class))).thenReturn(testEntity);

        Role result = roleService.createRole(roleWithNulls);

        assertNotNull(result);
        verify(roleRepository).existsByName("TEST_ROLE");
        verify(roleRepository).save(any(RoleEntity.class));
    }

    @Test
    void testUpdateRole_PartialUpdate() {
        Role partialUpdate = new Role();
        partialUpdate.setDescription("Новое описание");

        when(roleRepository.findById(testRole.getId())).thenReturn(Optional.of(testEntity));
        when(roleRepository.save(any(RoleEntity.class))).thenReturn(testEntity);

        Role result = roleService.updateRole(testRole.getId(), partialUpdate);

        assertNotNull(result);
        verify(roleRepository).findById(testRole.getId());
        verify(roleRepository, never()).existsByName(anyString());
        verify(roleRepository).save(any(RoleEntity.class));
    }
}