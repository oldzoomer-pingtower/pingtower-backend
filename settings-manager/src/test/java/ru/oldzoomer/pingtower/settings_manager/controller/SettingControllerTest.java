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
import ru.oldzoomer.pingtower.settings_manager.dto.Setting;
import ru.oldzoomer.pingtower.settings_manager.exception.EntityNotFoundException;
import ru.oldzoomer.pingtower.settings_manager.service.SettingService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SettingController.class)
@Import(SecurityConfig.class)
@WithMockUser(roles = "ADMIN")
class SettingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SettingService settingService;

    private Setting testSetting;

    @BeforeEach
    void setUp() {
        testSetting = new Setting();
        testSetting.setModule("pinger");
        testSetting.setKey("timeout");
        testSetting.setValue("5000");
        testSetting.setDescription("Таймаут для пинга в миллисекундах");
        testSetting.setCreatedAt(LocalDateTime.now());
        testSetting.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testGetAllSettings() throws Exception {
        when(settingService.getAllSettings()).thenReturn(List.of(testSetting));

        mockMvc.perform(get("/api/v1/settings"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].module").value("pinger"))
                .andExpect(jsonPath("$[0].key").value("timeout"))
                .andExpect(jsonPath("$[0].value").value("5000"));

        verify(settingService).getAllSettings();
    }

    @Test
    void testGetAllSettings_Empty() throws Exception {
        when(settingService.getAllSettings()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/settings"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(settingService).getAllSettings();
    }

    @Test
    void testGetSettingsByModule() throws Exception {
        when(settingService.getSettingsByModule("pinger")).thenReturn(List.of(testSetting));

        mockMvc.perform(get("/api/v1/settings/pinger"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].module").value("pinger"))
                .andExpect(jsonPath("$[0].key").value("timeout"));

        verify(settingService).getSettingsByModule("pinger");
    }

    @Test
    void testGetSettingsByModule_Empty() throws Exception {
        when(settingService.getSettingsByModule("nonexistent")).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/settings/nonexistent"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(settingService).getSettingsByModule("nonexistent");
    }

    @Test
    void testGetSetting_Exists() throws Exception {
        when(settingService.getSetting("pinger", "timeout")).thenReturn(Optional.of(testSetting));

        mockMvc.perform(get("/api/v1/settings/pinger/timeout"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.module").value("pinger"))
                .andExpect(jsonPath("$.key").value("timeout"))
                .andExpect(jsonPath("$.value").value("5000"));

        verify(settingService).getSetting("pinger", "timeout");
    }

    @Test
    void testGetSetting_NotExists() throws Exception {
        when(settingService.getSetting("pinger", "nonexistent")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/settings/pinger/nonexistent"))
                .andExpect(status().isNotFound());

        verify(settingService).getSetting("pinger", "nonexistent");
    }

    @Test
    void testCreateSetting() throws Exception {
        when(settingService.createSetting(any(Setting.class))).thenReturn(testSetting);

        mockMvc.perform(post("/api/v1/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSetting)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.module").value("pinger"))
                .andExpect(jsonPath("$.key").value("timeout"))
                .andExpect(jsonPath("$.value").value("5000"));

        verify(settingService).createSetting(any(Setting.class));
    }

    @Test
    void testCreateSetting_InvalidInput() throws Exception {
        Setting invalidSetting = new Setting();
        invalidSetting.setModule(""); // Пустой модуль

        mockMvc.perform(post("/api/v1/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSetting)))
                .andExpect(status().isBadRequest());

        verify(settingService, never()).createSetting(any(Setting.class));
    }

    @Test
    void testUpdateSetting_Success() throws Exception {
        when(settingService.updateSetting(eq("pinger"), eq("timeout"), any(Setting.class)))
                .thenReturn(testSetting);

        mockMvc.perform(put("/api/v1/settings/pinger/timeout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSetting)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.module").value("pinger"))
                .andExpect(jsonPath("$.key").value("timeout"));

        verify(settingService).updateSetting(eq("pinger"), eq("timeout"), any(Setting.class));
    }

    @Test
    void testUpdateSetting_NotFound() throws Exception {
        // Создаем объект Setting с теми же module и key, что и в пути запроса
        Setting settingForNotFoundTest = new Setting();
        settingForNotFoundTest.setModule("pinger");
        settingForNotFoundTest.setKey("nonexistent");
        settingForNotFoundTest.setValue("5000");
        settingForNotFoundTest.setDescription("Таймаут для пинга в миллисекундах");
        settingForNotFoundTest.setCreatedAt(LocalDateTime.now());
        settingForNotFoundTest.setUpdatedAt(LocalDateTime.now());

        when(settingService.updateSetting(eq("pinger"), eq("nonexistent"), any(Setting.class)))
                .thenThrow(new EntityNotFoundException("Setting not found"));

        mockMvc.perform(put("/api/v1/settings/pinger/nonexistent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(settingForNotFoundTest)))
                .andExpect(status().isNotFound());

        verify(settingService).updateSetting(eq("pinger"), eq("nonexistent"), any(Setting.class));
    }

    @Test
    void testUpdateSetting_InvalidInput() throws Exception {
        Setting invalidSetting = new Setting();
        invalidSetting.setModule("different_module"); // Модуль не совпадает с путем

        mockMvc.perform(put("/api/v1/settings/pinger/timeout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSetting)))
                .andExpect(status().isBadRequest());

        verify(settingService, never()).updateSetting(any(), any(), any());
    }

    @Test
    void testDeleteSetting_Success() throws Exception {
        doNothing().when(settingService).deleteSetting("pinger", "timeout");

        mockMvc.perform(delete("/api/v1/settings/pinger/timeout"))
                .andExpect(status().isNoContent());

        verify(settingService).deleteSetting("pinger", "timeout");
    }

    @Test
    void testDeleteSetting_NotFound() throws Exception {
        doThrow(new EntityNotFoundException("Setting not found")).when(settingService).deleteSetting("pinger", "nonexistent");

        mockMvc.perform(delete("/api/v1/settings/pinger/nonexistent"))
                .andExpect(status().isNotFound());

        verify(settingService).deleteSetting("pinger", "nonexistent");
    }

    @Test
    void testCreateSetting_MissingRequiredFields() throws Exception {
        Setting settingWithoutModule = new Setting();
        settingWithoutModule.setKey("test");
        settingWithoutModule.setValue("value");

        mockMvc.perform(post("/api/v1/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(settingWithoutModule)))
                .andExpect(status().isBadRequest());

        verify(settingService, never()).createSetting(any(Setting.class));
    }

    @Test
    void testUpdateSetting_PathMismatch() throws Exception {
        Setting settingWithDifferentPath = new Setting();
        settingWithDifferentPath.setModule("different_module");
        settingWithDifferentPath.setKey("different_key");
        settingWithDifferentPath.setValue("value");

        mockMvc.perform(put("/api/v1/settings/pinger/timeout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(settingWithDifferentPath)))
                .andExpect(status().isBadRequest());

        verify(settingService, never()).updateSetting(any(), any(), any());
    }
}