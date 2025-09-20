package ru.oldzoomer.pingtower.notificator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ru.oldzoomer.pingtower.notificator.config.SecurityConfig;
import ru.oldzoomer.pingtower.notificator.dto.NotificationRuleDTO;
import ru.oldzoomer.pingtower.notificator.service.RuleManagementService;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RuleController.class)
@Import(SecurityConfig.class)
@WithMockUser(roles = "ADMIN")
class RuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RuleManagementService ruleManagementService;

    @Test
    void testGetRules_EmptyList() throws Exception {
        when(ruleManagementService.getRules(1, 20))
                .thenReturn(Map.of(
                        "rules", List.of(),
                        "page", 1,
                        "size", 20,
                        "total", 0
                ));

        mockMvc.perform(get("/api/v1/notificator/rules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rules").isArray())
                .andExpect(jsonPath("$.rules").isEmpty())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.total").value(0));
    }

    @Test
    void testGetRules_WithPagination() throws Exception {
        NotificationRuleDTO rule = new NotificationRuleDTO();
        rule.setId("rule-1");
        rule.setName("Test Rule");
        rule.setEnabled(true);

        when(ruleManagementService.getRules(2, 10))
                .thenReturn(Map.of(
                        "rules", List.of(rule),
                        "page", 2,
                        "size", 10,
                        "total", 1
                ));

        mockMvc.perform(get("/api/v1/notificator/rules")
                        .param("page", "2")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rules").isArray())
                .andExpect(jsonPath("$.rules[0].id").value("rule-1"))
                .andExpect(jsonPath("$.rules[0].name").value("Test Rule"))
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    void testGetRule_NotFound() throws Exception {
        when(ruleManagementService.getRule("nonexistent")).thenReturn(null);

        mockMvc.perform(get("/api/v1/notificator/rules/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetRule_Found() throws Exception {
        NotificationRuleDTO rule = new NotificationRuleDTO();
        rule.setId("rule-1");
        rule.setName("Test Rule");
        rule.setEnabled(true);

        when(ruleManagementService.getRule("rule-1")).thenReturn(rule);

        mockMvc.perform(get("/api/v1/notificator/rules/rule-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("rule-1"))
                .andExpect(jsonPath("$.name").value("Test Rule"))
                .andExpect(jsonPath("$.enabled").value(true));
    }

    @Test
    void testCreateRule_Success() throws Exception {
        NotificationRuleDTO request = new NotificationRuleDTO();
        request.setName("Test Rule");
        request.setEnabled(true);
        request.setConditions(List.of());
        request.setActions(List.of());

        NotificationRuleDTO response = new NotificationRuleDTO();
        response.setId("rule-123");
        response.setName("Test Rule");
        response.setEnabled(true);
        response.setCreatedAt("2024-01-15T10:30:00");
        response.setUpdatedAt("2024-01-15T10:30:00");

        when(ruleManagementService.createRule(any(NotificationRuleDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/notificator/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("rule-123"))
                .andExpect(jsonPath("$.name").value("Test Rule"))
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void testUpdateRule_Success() throws Exception {
        NotificationRuleDTO request = new NotificationRuleDTO();
        request.setName("Updated Rule");
        request.setEnabled(false);
        request.setConditions(List.of());
        request.setActions(List.of());

        NotificationRuleDTO response = new NotificationRuleDTO();
        response.setId("rule-123");
        response.setName("Updated Rule");
        response.setEnabled(false);
        response.setUpdatedAt("2024-01-15T11:30:00");

        when(ruleManagementService.updateRule(eq("rule-123"), any(NotificationRuleDTO.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/notificator/rules/rule-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("rule-123"))
                .andExpect(jsonPath("$.name").value("Updated Rule"))
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void testUpdateRule_NotFound() throws Exception {
        NotificationRuleDTO request = new NotificationRuleDTO();
        request.setName("Updated Rule");
        request.setEnabled(false);
        request.setConditions(List.of());
        request.setActions(List.of());

        when(ruleManagementService.updateRule(eq("nonexistent"), any(NotificationRuleDTO.class)))
                .thenReturn(null);

        mockMvc.perform(put("/api/v1/notificator/rules/nonexistent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteRule_Success() throws Exception {
        when(ruleManagementService.deleteRule("rule-123")).thenReturn(true);

        mockMvc.perform(delete("/api/v1/notificator/rules/rule-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Rule deleted successfully"));
    }

    @Test
    void testDeleteRule_NotFound() throws Exception {
        when(ruleManagementService.deleteRule("nonexistent")).thenReturn(false);

        mockMvc.perform(delete("/api/v1/notificator/rules/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateRule_ValidationError() throws Exception {
        // Test with invalid data (empty name)
        NotificationRuleDTO request = new NotificationRuleDTO();
        request.setName("");
        request.setEnabled(true);
        request.setConditions(List.of());
        request.setActions(List.of());

        mockMvc.perform(post("/api/v1/notificator/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}