package ru.oldzoomer.pingtower.settings_manager.controller;

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
import ru.oldzoomer.pingtower.settings_manager.exception.EntityNotFoundException;
import ru.oldzoomer.pingtower.settings_manager.service.UserRoleService;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserRoleController.class)
@Import(SecurityConfig.class)
@WithMockUser(roles = "ADMIN")
class UserRoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRoleService userRoleService;

    private UUID testUserId;
    private UUID testRoleId;
    private UUID testRoleId2;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testRoleId = UUID.randomUUID();
        testRoleId2 = UUID.randomUUID();
    }

    @Test
    void testGetRolesForUser() throws Exception {
        when(userRoleService.getRolesForUser(testUserId)).thenReturn(List.of(testRoleId, testRoleId2));

        mockMvc.perform(get("/api/v1/user-roles/user/{userId}", testUserId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value(testRoleId.toString()))
                .andExpect(jsonPath("$[1]").value(testRoleId2.toString()));

        verify(userRoleService).getRolesForUser(testUserId);
    }

    @Test
    void testGetRolesForUser_Empty() throws Exception {
        when(userRoleService.getRolesForUser(testUserId)).thenThrow(new EntityNotFoundException("User not found"));

        mockMvc.perform(get("/api/v1/user-roles/user/{userId}", testUserId))
                .andExpect(status().isNotFound());

        verify(userRoleService).getRolesForUser(testUserId);
    }

    @Test
    void testGetUsersWithRole() throws Exception {
        when(userRoleService.getUsersWithRole(testRoleId)).thenReturn(List.of(testUserId));

        mockMvc.perform(get("/api/v1/user-roles/role/{roleId}", testRoleId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value(testUserId.toString()));

        verify(userRoleService).getUsersWithRole(testRoleId);
    }

    @Test
    void testGetUsersWithRole_Empty() throws Exception {
        when(userRoleService.getUsersWithRole(testRoleId)).thenThrow(new EntityNotFoundException("Role not found"));

        mockMvc.perform(get("/api/v1/user-roles/role/{roleId}", testRoleId))
                .andExpect(status().isNotFound());

        verify(userRoleService).getUsersWithRole(testRoleId);
    }

    @Test
    void testAssignRoleToUser() throws Exception {
        doNothing().when(userRoleService).assignRoleToUser(testUserId, testRoleId);

        mockMvc.perform(post("/api/v1/user-roles/{userId}/{roleId}", testUserId, testRoleId))
                .andExpect(status().isOk());

        verify(userRoleService).assignRoleToUser(testUserId, testRoleId);
    }

    @Test
    void testAssignRoleToUser_ServiceException() throws Exception {
        doThrow(new RuntimeException("User or role not found"))
                .when(userRoleService).assignRoleToUser(testUserId, testRoleId);

        mockMvc.perform(post("/api/v1/user-roles/{userId}/{roleId}", testUserId, testRoleId))
                .andExpect(status().isInternalServerError());

        verify(userRoleService).assignRoleToUser(testUserId, testRoleId);
    }

    @Test
    void testRemoveRoleFromUser() throws Exception {
        doNothing().when(userRoleService).removeRoleFromUser(testUserId, testRoleId);

        mockMvc.perform(delete("/api/v1/user-roles/{userId}/{roleId}", testUserId, testRoleId))
                .andExpect(status().isNoContent());

        verify(userRoleService).removeRoleFromUser(testUserId, testRoleId);
    }

    @Test
    void testRemoveRoleFromUser_NotFound() throws Exception {
        doThrow(new EntityNotFoundException("Role assignment not found"))
                .when(userRoleService).removeRoleFromUser(testUserId, testRoleId);

        mockMvc.perform(delete("/api/v1/user-roles/{userId}/{roleId}", testUserId, testRoleId))
                .andExpect(status().isNotFound());

        verify(userRoleService).removeRoleFromUser(testUserId, testRoleId);
    }

    @Test
    void testRemoveAllRolesFromUser() throws Exception {
        doNothing().when(userRoleService).removeAllRolesFromUser(testUserId);

        mockMvc.perform(delete("/api/v1/user-roles/user/{userId}", testUserId))
                .andExpect(status().isNoContent());

        verify(userRoleService).removeAllRolesFromUser(testUserId);
    }

    @Test
    void testRemoveAllRolesFromUser_NotFound() throws Exception {
        doThrow(new EntityNotFoundException("User not found"))
                .when(userRoleService).removeAllRolesFromUser(testUserId);

        mockMvc.perform(delete("/api/v1/user-roles/user/{userId}", testUserId))
                .andExpect(status().isNotFound());

        verify(userRoleService).removeAllRolesFromUser(testUserId);
    }

    @Test
    void testAssignRoleToUser_InvalidUUID() throws Exception {
        mockMvc.perform(post("/api/v1/user-roles/{userId}/{roleId}", "invalid-uuid", testRoleId))
                .andExpect(status().isBadRequest());

        verify(userRoleService, never()).assignRoleToUser(any(), any());
    }

    @Test
    void testRemoveRoleFromUser_InvalidUUID() throws Exception {
        mockMvc.perform(delete("/api/v1/user-roles/{userId}/{roleId}", "invalid-uuid", testRoleId))
                .andExpect(status().isBadRequest());

        verify(userRoleService, never()).removeRoleFromUser(any(), any());
    }

    @Test
    void testGetRolesForUser_InvalidUUID() throws Exception {
        mockMvc.perform(get("/api/v1/user-roles/user/{userId}", "invalid-uuid"))
                .andExpect(status().isBadRequest());

        verify(userRoleService, never()).getRolesForUser(any());
    }

    @Test
    void testGetUsersWithRole_InvalidUUID() throws Exception {
        mockMvc.perform(get("/api/v1/user-roles/role/{roleId}", "invalid-uuid"))
                .andExpect(status().isBadRequest());

        verify(userRoleService, never()).getUsersWithRole(any());
    }

    @Test
    void testRemoveAllRolesFromUser_InvalidUUID() throws Exception {
        mockMvc.perform(delete("/api/v1/user-roles/user/{userId}", "invalid-uuid"))
                .andExpect(status().isBadRequest());

        verify(userRoleService, never()).removeAllRolesFromUser(any());
    }

    @Test
    void testAssignRoleToUser_SameUserRoleTwice() throws Exception {
        doNothing().when(userRoleService).assignRoleToUser(testUserId, testRoleId);

        // Первое назначение
        mockMvc.perform(post("/api/v1/user-roles/{userId}/{roleId}", testUserId, testRoleId))
                .andExpect(status().isOk());

        // Второе назначение (должно работать нормально)
        mockMvc.perform(post("/api/v1/user-roles/{userId}/{roleId}", testUserId, testRoleId))
                .andExpect(status().isOk());

        verify(userRoleService, times(2)).assignRoleToUser(testUserId, testRoleId);
    }
}