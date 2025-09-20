package ru.oldzoomer.pingtower.settings_manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ru.oldzoomer.pingtower.settings_manager.config.SecurityConfig;
import ru.oldzoomer.pingtower.settings_manager.dto.Role;
import ru.oldzoomer.pingtower.settings_manager.service.RoleService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoleController.class)
@Import(SecurityConfig.class)
@WithMockUser(roles = "ADMIN")
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RoleService roleService;

    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setId(UUID.randomUUID());
        testRole.setName("ADMIN");
        testRole.setDescription("Администратор системы");
        testRole.setCreatedAt(LocalDateTime.now());
        testRole.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testGetAllRoles() throws Exception {
        when(roleService.getAllRoles()).thenReturn(List.of(testRole));

        mockMvc.perform(get("/api/v1/roles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("ADMIN"))
                .andExpect(jsonPath("$[0].description").value("Администратор системы"));

        verify(roleService).getAllRoles();
    }

    @Test
    void testGetAllRoles_Empty() throws Exception {
        when(roleService.getAllRoles()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/roles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(roleService).getAllRoles();
    }

    @Test
    void testGetRole_Exists() throws Exception {
        when(roleService.getRoleById(testRole.getId())).thenReturn(Optional.of(testRole));

        mockMvc.perform(get("/api/v1/roles/{id}", testRole.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("ADMIN"))
                .andExpect(jsonPath("$.description").value("Администратор системы"));

        verify(roleService).getRoleById(testRole.getId());
    }

    @Test
    void testGetRole_NotExists() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        when(roleService.getRoleById(nonExistentId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/roles/{id}", nonExistentId))
                .andExpect(status().isNotFound());

        verify(roleService).getRoleById(nonExistentId);
    }

    @Test
    void testCreateRole_Success() throws Exception {
        when(roleService.createRole(any(Role.class))).thenReturn(testRole);

        mockMvc.perform(post("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRole)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("ADMIN"))
                .andExpect(jsonPath("$.description").value("Администратор системы"));

        verify(roleService).createRole(any(Role.class));
    }

    @Test
    void testCreateRole_InvalidInput() throws Exception {
        Role invalidRole = new Role();
        invalidRole.setName(""); // Пустое имя роли

        mockMvc.perform(post("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRole)))
                .andExpect(status().isBadRequest());

        verify(roleService, never()).createRole(any(Role.class));
    }

    @Test
    void testCreateRole_ServiceException() throws Exception {
        when(roleService.createRole(any(Role.class))).thenThrow(new RuntimeException("Role already exists"));

        mockMvc.perform(post("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRole)))
                .andExpect(status().isBadRequest());

        verify(roleService).createRole(any(Role.class));
    }

    @Test
    void testUpdateRole_Success() throws Exception {
        when(roleService.updateRole(eq(testRole.getId()), any(Role.class))).thenReturn(testRole);

        mockMvc.perform(put("/api/v1/roles/{id}", testRole.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRole)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("ADMIN"));

        verify(roleService).updateRole(eq(testRole.getId()), any(Role.class));
    }

    @Test
    void testUpdateRole_NotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        when(roleService.updateRole(eq(nonExistentId), any(Role.class)))
                .thenThrow(new RuntimeException("Role not found"));

        // Создаем роль с тем же ID, что и в пути, чтобы сервис был вызван
        Role roleWithNonExistentId = new Role();
        roleWithNonExistentId.setId(nonExistentId);
        roleWithNonExistentId.setName(testRole.getName());
        roleWithNonExistentId.setDescription(testRole.getDescription());
        roleWithNonExistentId.setCreatedAt(testRole.getCreatedAt());
        roleWithNonExistentId.setUpdatedAt(testRole.getUpdatedAt());
        
        mockMvc.perform(put("/api/v1/roles/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleWithNonExistentId)))
                .andExpect(status().isNotFound());

        verify(roleService).updateRole(eq(nonExistentId), any(Role.class));
    }

    @Test
    void testUpdateRole_InvalidInput() throws Exception {
        Role invalidRole = new Role();
        invalidRole.setName(""); // Пустое имя роли

        mockMvc.perform(put("/api/v1/roles/{id}", testRole.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRole)))
                .andExpect(status().isBadRequest());

        verify(roleService, never()).updateRole(any(), any());
    }

    @Test
    void testDeleteRole_Success() throws Exception {
        doNothing().when(roleService).deleteRole(testRole.getId());

        mockMvc.perform(delete("/api/v1/roles/{id}", testRole.getId()))
                .andExpect(status().isNoContent());

        verify(roleService).deleteRole(testRole.getId());
    }

    @Test
    void testDeleteRole_NotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        doThrow(new RuntimeException("Role not found")).when(roleService).deleteRole(nonExistentId);

        mockMvc.perform(delete("/api/v1/roles/{id}", nonExistentId))
                .andExpect(status().isNotFound());

        verify(roleService).deleteRole(nonExistentId);
    }

    @Test
    void testCreateRole_MissingRequiredFields() throws Exception {
        Role roleWithoutName = new Role();
        roleWithoutName.setDescription("Описание без имени");

        mockMvc.perform(post("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleWithoutName)))
                .andExpect(status().isBadRequest());

        verify(roleService, never()).createRole(any(Role.class));
    }

    @Test
    void testUpdateRole_PathMismatch() throws Exception {
        Role roleWithDifferentId = new Role();
        roleWithDifferentId.setId(UUID.randomUUID()); // Другой ID
        roleWithDifferentId.setName("DIFFERENT");

        mockMvc.perform(put("/api/v1/roles/{id}", testRole.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleWithDifferentId)))
                .andExpect(status().isBadRequest());

        verify(roleService, never()).updateRole(any(), any());
    }

    @Test
    void testUpdateRole_PartialUpdate() throws Exception {
        Role partialUpdate = new Role();
        partialUpdate.setDescription("Новое описание");

        when(roleService.updateRole(eq(testRole.getId()), any(Role.class))).thenReturn(testRole);

        mockMvc.perform(put("/api/v1/roles/{id}", testRole.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partialUpdate)))
                .andExpect(status().isOk());

        verify(roleService).updateRole(eq(testRole.getId()), any(Role.class));
    }
}