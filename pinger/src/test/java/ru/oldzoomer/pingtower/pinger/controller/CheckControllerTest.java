package ru.oldzoomer.pingtower.pinger.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.oldzoomer.pingtower.pinger.dto.CheckConfigurationDTO;
import ru.oldzoomer.pingtower.pinger.service.CheckManagementService;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CheckControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CheckManagementService checkManagementService;

    @InjectMocks
    private CheckController checkController;

    private CheckConfigurationDTO testCheck;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(checkController).build();
        objectMapper = new ObjectMapper();

        testCheck = new CheckConfigurationDTO();
        testCheck.setId("test-check-1");
        testCheck.setType("HTTP");
        testCheck.setResourceUrl("https://example.com");
        testCheck.setFrequency(60000L);
        testCheck.setTimeout(5000);
        testCheck.setExpectedStatusCode(200);
        testCheck.setExpectedResponseTime(1000L);
        testCheck.setValidateSsl(true);
        testCheck.setCreatedAt("2024-01-15T10:30:00");
        testCheck.setUpdatedAt("2024-01-15T10:30:00");
    }

    @Test
    void testGetChecks() throws Exception {
        Map<String, Object> response = Map.of(
                "checks", new CheckConfigurationDTO[]{testCheck},
                "total", 1,
                "page", 1,
                "size", 20
        );

        when(checkManagementService.getChecks(1, 20)).thenReturn(response);

        mockMvc.perform(get("/api/v1/pinger/checks")
                        .param("page", "1")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.checks[0].id").value("test-check-1"));

        verify(checkManagementService).getChecks(1, 20);
    }

    @Test
    void testGetChecks_DefaultParameters() throws Exception {
        Map<String, Object> response = Map.of(
                "checks", new CheckConfigurationDTO[]{},
                "total", 0,
                "page", 1,
                "size", 20
        );

        when(checkManagementService.getChecks(1, 20)).thenReturn(response);

        mockMvc.perform(get("/api/v1/pinger/checks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(20));

        verify(checkManagementService).getChecks(1, 20);
    }

    @Test
    void testGetCheck_Exists() throws Exception {
        when(checkManagementService.getCheck("test-check-1")).thenReturn(testCheck);

        mockMvc.perform(get("/api/v1/pinger/checks/test-check-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test-check-1"))
                .andExpect(jsonPath("$.type").value("HTTP"))
                .andExpect(jsonPath("$.resourceUrl").value("https://example.com"));

        verify(checkManagementService).getCheck("test-check-1");
    }

    @Test
    void testGetCheck_NotFound() throws Exception {
        when(checkManagementService.getCheck("nonexistent")).thenReturn(null);

        mockMvc.perform(get("/api/v1/pinger/checks/nonexistent"))
                .andExpect(status().isNotFound());

        verify(checkManagementService).getCheck("nonexistent");
    }

    @Test
    void testCreateCheck() throws Exception {
        when(checkManagementService.createCheck(any(CheckConfigurationDTO.class))).thenReturn(testCheck);

        mockMvc.perform(post("/api/v1/pinger/checks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCheck)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test-check-1"))
                .andExpect(jsonPath("$.type").value("HTTP"));

        verify(checkManagementService).createCheck(any(CheckConfigurationDTO.class));
    }

    @Test
    void testUpdateCheck_Exists() throws Exception {
        CheckConfigurationDTO updatedCheck = new CheckConfigurationDTO();
        updatedCheck.setId("test-check-1");
        updatedCheck.setType("HTTPS");
        updatedCheck.setResourceUrl("https://updated.example.com");

        when(checkManagementService.updateCheck(eq("test-check-1"), any(CheckConfigurationDTO.class)))
                .thenReturn(updatedCheck);

        mockMvc.perform(put("/api/v1/pinger/checks/test-check-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCheck)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test-check-1"))
                .andExpect(jsonPath("$.type").value("HTTPS"))
                .andExpect(jsonPath("$.resourceUrl").value("https://updated.example.com"));

        verify(checkManagementService).updateCheck(eq("test-check-1"), any(CheckConfigurationDTO.class));
    }

    @Test
    void testUpdateCheck_NotFound() throws Exception {
        when(checkManagementService.updateCheck(eq("nonexistent"), any(CheckConfigurationDTO.class)))
                .thenReturn(null);

        mockMvc.perform(put("/api/v1/pinger/checks/nonexistent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCheck)))
                .andExpect(status().isNotFound());

        verify(checkManagementService).updateCheck(eq("nonexistent"), any(CheckConfigurationDTO.class));
    }

    @Test
    void testDeleteCheck_Exists() throws Exception {
        when(checkManagementService.deleteCheck("test-check-1")).thenReturn(true);

        mockMvc.perform(delete("/api/v1/pinger/checks/test-check-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Check deleted successfully"));

        verify(checkManagementService).deleteCheck("test-check-1");
    }

    @Test
    void testDeleteCheck_NotFound() throws Exception {
        when(checkManagementService.deleteCheck("nonexistent")).thenReturn(false);

        mockMvc.perform(delete("/api/v1/pinger/checks/nonexistent"))
                .andExpect(status().isNotFound());

        verify(checkManagementService).deleteCheck("nonexistent");
    }

    @Test
    void testCreateCheck_InvalidData() throws Exception {
        CheckConfigurationDTO invalidCheck = new CheckConfigurationDTO();
        invalidCheck.setType("HTTP");
        // Missing required fields like resourceUrl

        mockMvc.perform(post("/api/v1/pinger/checks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCheck)))
                .andExpect(status().isOk()); // Controller doesn't validate, service does

        verify(checkManagementService).createCheck(any(CheckConfigurationDTO.class));
    }

    @Test
    void testUpdateCheck_MismatchedId() throws Exception {
        CheckConfigurationDTO checkWithDifferentId = new CheckConfigurationDTO();
        checkWithDifferentId.setId("different-id");
        checkWithDifferentId.setType("HTTP");
        checkWithDifferentId.setResourceUrl("https://example.com");

        when(checkManagementService.updateCheck(eq("test-check-1"), any(CheckConfigurationDTO.class)))
                .thenReturn(checkWithDifferentId);

        mockMvc.perform(put("/api/v1/pinger/checks/test-check-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkWithDifferentId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("different-id"));

        verify(checkManagementService).updateCheck(eq("test-check-1"), any(CheckConfigurationDTO.class));
    }
}