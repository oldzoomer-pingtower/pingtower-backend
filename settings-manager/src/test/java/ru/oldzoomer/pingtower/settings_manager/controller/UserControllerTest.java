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
import ru.oldzoomer.pingtower.settings_manager.dto.User;
import ru.oldzoomer.pingtower.settings_manager.exception.EntityNotFoundException;
import ru.oldzoomer.pingtower.settings_manager.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
@WithMockUser(roles = "ADMIN")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(testUser));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].email").value("test@example.com"));

        verify(userService).getAllUsers();
    }

    @Test
    void testGetAllUsers_Empty() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(userService).getAllUsers();
    }

    @Test
    void testGetUser_Exists() throws Exception {
        when(userService.getUserById(testUser.getId())).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/v1/users/{id}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService).getUserById(testUser.getId());
    }

    @Test
    void testGetUser_NotExists() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        when(userService.getUserById(nonExistentId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/users/{id}", nonExistentId))
                .andExpect(status().isNotFound());

        verify(userService).getUserById(nonExistentId);
    }

    @Test
    void testCreateUser_Success() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService).createUser(any(User.class));
    }

    @Test
    void testCreateUser_InvalidInput() throws Exception {
        User invalidUser = new User();
        invalidUser.setUsername(""); // Пустое имя пользователя

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    void testCreateUser_ServiceException() throws Exception {
        when(userService.createUser(any(User.class))).thenThrow(new RuntimeException("User already exists"));

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isBadRequest());

        verify(userService).createUser(any(User.class));
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        when(userService.updateUser(eq(testUser.getId()), any(User.class))).thenReturn(testUser);

        mockMvc.perform(put("/api/v1/users/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(userService).updateUser(eq(testUser.getId()), any(User.class));
    }

    @Test
    void testUpdateUser_NotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        // Создаем объект User с тем же ID, что и в пути запроса
        User userForNotFoundTest = new User();
        userForNotFoundTest.setId(nonExistentId);
        userForNotFoundTest.setUsername("testuser");
        userForNotFoundTest.setEmail("test@example.com");
        userForNotFoundTest.setFirstName("Test");
        userForNotFoundTest.setLastName("User");
        userForNotFoundTest.setCreatedAt(LocalDateTime.now());
        userForNotFoundTest.setUpdatedAt(LocalDateTime.now());

        when(userService.updateUser(eq(nonExistentId), any(User.class)))
                .thenThrow(new EntityNotFoundException("User not found"));

        mockMvc.perform(put("/api/v1/users/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userForNotFoundTest)))
                .andExpect(status().isNotFound());

        verify(userService).updateUser(eq(nonExistentId), any(User.class));
    }

    @Test
    void testUpdateUser_InvalidInput() throws Exception {
        User invalidUser = new User();
        invalidUser.setUsername(""); // Пустое имя пользователя

        mockMvc.perform(put("/api/v1/users/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(any(), any());
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(testUser.getId());

        mockMvc.perform(delete("/api/v1/users/{id}", testUser.getId()))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(testUser.getId());
    }

    @Test
    void testDeleteUser_NotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        doThrow(new EntityNotFoundException("User not found")).when(userService).deleteUser(nonExistentId);

        mockMvc.perform(delete("/api/v1/users/{id}", nonExistentId))
                .andExpect(status().isNotFound());

        verify(userService).deleteUser(nonExistentId);
    }

    @Test
    void testCreateUser_MissingRequiredFields() throws Exception {
        User userWithoutUsername = new User();
        userWithoutUsername.setEmail("test@example.com");

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userWithoutUsername)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    void testUpdateUser_PathMismatch() throws Exception {
        User userWithDifferentId = new User();
        userWithDifferentId.setId(UUID.randomUUID()); // Другой ID
        userWithDifferentId.setUsername("different");

        mockMvc.perform(put("/api/v1/users/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userWithDifferentId)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(any(), any());
    }
}