package ru.oldzoomer.pingtower.statistics.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.oldzoomer.pingtower.statistics.config.SecurityConfig;
import ru.oldzoomer.pingtower.statistics.dto.CheckResult;
import ru.oldzoomer.pingtower.statistics.service.StatisticsRetrievalService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatisticsController.class)
@Import(SecurityConfig.class)
@WithMockUser(roles = "ADMIN")
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StatisticsRetrievalService statisticsRetrievalService;

    private CheckResult testCheckResult;

    @BeforeEach
    void setUp() {
        LocalDateTime testTimestamp = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
        
        testCheckResult = new CheckResult();
        testCheckResult.setCheckId("test-check-1");
        testCheckResult.setTimestamp(testTimestamp);
        testCheckResult.setStatus("UP");
        testCheckResult.setResponseTime(150L);
        testCheckResult.setHttpStatusCode(200);
        testCheckResult.setErrorMessage(null);
    }

    @Test
    void testGetLatestCheckResult_Success() throws Exception {
        // Given
        when(statisticsRetrievalService.getLatestCheckResult("test-check-1"))
                .thenReturn(testCheckResult);

        // When & Then
        mockMvc.perform(get("/api/v1/statistics/checks/test-check-1/latest")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.checkId").value("test-check-1"))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.responseTime").value(150))
                .andExpect(jsonPath("$.httpStatusCode").value(200));
    }

    @Test
    void testGetLatestCheckResult_NotFound() throws Exception {
        // Given
        when(statisticsRetrievalService.getLatestCheckResult("non-existent-check"))
                .thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/v1/statistics/checks/non-existent-check/latest")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetLatestCheckResult_InternalServerError() throws Exception {
        // Given
        when(statisticsRetrievalService.getLatestCheckResult("test-check-1"))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/api/v1/statistics/checks/test-check-1/latest")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetCheckHistory_Success() throws Exception {
        // Given
        List<CheckResult> history = Arrays.asList(testCheckResult, testCheckResult);
        when(statisticsRetrievalService.getCheckHistory(eq("test-check-1"), any(), any(), anyInt(), anyInt()))
                .thenReturn(history);

        // When & Then
        mockMvc.perform(get("/api/v1/statistics/checks/test-check-1/history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("limit", "100")
                        .param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].checkId").value("test-check-1"))
                .andExpect(jsonPath("$[1].checkId").value("test-check-1"));
    }

    @Test
    void testGetCheckHistory_WithDateTimeFilters() throws Exception {
        // Given
        List<CheckResult> history = Collections.singletonList(testCheckResult);
        when(statisticsRetrievalService.getCheckHistory(eq("test-check-1"), any(), any(), anyInt(), anyInt()))
                .thenReturn(history);

        // When & Then
        mockMvc.perform(get("/api/v1/statistics/checks/test-check-1/history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("from", "2024-01-15T10:00:00")
                        .param("to", "2024-01-15T11:00:00")
                        .param("limit", "50")
                        .param("offset", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetCheckHistory_EmptyHistory() throws Exception {
        // Given
        when(statisticsRetrievalService.getCheckHistory(eq("test-check-1"), any(), any(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/v1/statistics/checks/test-check-1/history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetCheckHistory_InternalServerError() throws Exception {
        // Given
        when(statisticsRetrievalService.getCheckHistory(eq("test-check-1"), any(), any(), anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/api/v1/statistics/checks/test-check-1/history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetAggregatedData_Success() throws Exception {
        // Given
        Object aggregatedData = new Object() {
            @SuppressWarnings("unused")
            public final String checkId = "test-check-1";
            @SuppressWarnings("unused")
            public final String interval = "hourly";
            @SuppressWarnings("unused")
            public final int count = 10;
        };
        
        when(statisticsRetrievalService.getAggregatedData(eq("test-check-1"), eq("hourly"), any(), any()))
                .thenReturn(aggregatedData);

        // When & Then
        mockMvc.perform(get("/api/v1/statistics/checks/test-check-1/aggregated")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("interval", "hourly")
                        .param("from", "2024-01-15T10:00:00")
                        .param("to", "2024-01-15T11:00:00"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.checkId").value("test-check-1"))
                .andExpect(jsonPath("$.interval").value("hourly"))
                .andExpect(jsonPath("$.count").value(10));
    }

    @Test
    void testGetAggregatedData_NotFound() throws Exception {
        // Given
        when(statisticsRetrievalService.getAggregatedData(eq("test-check-1"), eq("hourly"), any(), any()))
                .thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/v1/statistics/checks/test-check-1/aggregated")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("interval", "hourly"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAggregatedData_InternalServerError() throws Exception {
        // Given
        when(statisticsRetrievalService.getAggregatedData(eq("test-check-1"), eq("hourly"), any(), any()))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/api/v1/statistics/checks/test-check-1/aggregated")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("interval", "hourly"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetDashboardData_Success() throws Exception {
        // Given
        Object dashboardData = new Object() {
            @SuppressWarnings("unused")
            public final int totalChecks = 100;
            @SuppressWarnings("unused")
            public final int upChecks = 95;
            @SuppressWarnings("unused")
            public final int downChecks = 5;
        };
        
        when(statisticsRetrievalService.getDashboardData())
                .thenReturn(dashboardData);

        // When & Then
        mockMvc.perform(get("/api/v1/statistics/dashboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalChecks").value(100))
                .andExpect(jsonPath("$.upChecks").value(95))
                .andExpect(jsonPath("$.downChecks").value(5));
    }

    @Test
    void testGetDashboardData_InternalServerError() throws Exception {
        // Given
        when(statisticsRetrievalService.getDashboardData())
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/api/v1/statistics/dashboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetAggregatedData_WithoutOptionalParams() throws Exception {
        // Given
        Object aggregatedData = new Object() {
            @SuppressWarnings("unused")
            public final String checkId = "test-check-1";
            @SuppressWarnings("unused")
            public final String interval = "daily";
            @SuppressWarnings("unused")
            public final int count = 5;
        };
        
        when(statisticsRetrievalService.getAggregatedData(eq("test-check-1"), eq("daily"), any(), any()))
                .thenReturn(aggregatedData);

        // When & Then
        mockMvc.perform(get("/api/v1/statistics/checks/test-check-1/aggregated")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("interval", "daily"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.checkId").value("test-check-1"))
                .andExpect(jsonPath("$.interval").value("daily"))
                .andExpect(jsonPath("$.count").value(5));
    }
}