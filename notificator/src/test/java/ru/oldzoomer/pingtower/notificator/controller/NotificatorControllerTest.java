package ru.oldzoomer.pingtower.notificator.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import ru.oldzoomer.pingtower.notificator.config.SecurityConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificatorController.class)
@Import(SecurityConfig.class)
@WithMockUser(roles = "ADMIN")
class NotificatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetHealth() throws Exception {
        mockMvc.perform(get("/api/v1/notificator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("Notificator"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testGetStats() throws Exception {
        mockMvc.perform(get("/api/v1/notificator/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeChannels").value(0))
                .andExpect(jsonPath("$.activeRules").value(0))
                .andExpect(jsonPath("$.notificationsSent").value(0))
                .andExpect(jsonPath("$.notificationsFailed").value(0))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}