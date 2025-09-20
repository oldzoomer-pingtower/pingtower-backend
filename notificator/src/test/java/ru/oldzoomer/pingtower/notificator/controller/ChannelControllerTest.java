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
import ru.oldzoomer.pingtower.notificator.dto.EmailChannelConfig;
import ru.oldzoomer.pingtower.notificator.dto.NotificationChannelDTO;
import ru.oldzoomer.pingtower.notificator.service.ChannelManagementService;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChannelController.class)
@Import(SecurityConfig.class)
@WithMockUser(roles = "ADMIN")
class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChannelManagementService channelManagementService;

    @Test
    void testGetChannels_EmptyList() throws Exception {
        when(channelManagementService.getChannels(1, 20, null))
                .thenReturn(Map.of(
                        "channels", List.of(),
                        "page", 1,
                        "size", 20,
                        "total", 0
                ));

        mockMvc.perform(get("/api/v1/notificator/channels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.channels").isArray())
                .andExpect(jsonPath("$.channels").isEmpty())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.total").value(0));
    }

    @Test
    void testGetChannels_WithPagination() throws Exception {
        NotificationChannelDTO channel = new NotificationChannelDTO();
        channel.setId("channel-1");
        channel.setName("Test Channel");
        channel.setType("EMAIL");
        channel.setEnabled(true);

        when(channelManagementService.getChannels(2, 10, "EMAIL"))
                .thenReturn(Map.of(
                        "channels", List.of(channel),
                        "page", 2,
                        "size", 10,
                        "total", 1
                ));

        mockMvc.perform(get("/api/v1/notificator/channels")
                        .param("page", "2")
                        .param("size", "10")
                        .param("type", "EMAIL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.channels").isArray())
                .andExpect(jsonPath("$.channels[0].id").value("channel-1"))
                .andExpect(jsonPath("$.channels[0].name").value("Test Channel"))
                .andExpect(jsonPath("$.channels[0].type").value("EMAIL"))
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    void testGetChannel_NotFound() throws Exception {
        when(channelManagementService.getChannel("nonexistent")).thenReturn(null);

        mockMvc.perform(get("/api/v1/notificator/channels/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetChannel_Found() throws Exception {
        NotificationChannelDTO channel = new NotificationChannelDTO();
        channel.setId("channel-1");
        channel.setName("Test Channel");
        channel.setType("EMAIL");
        channel.setEnabled(true);

        when(channelManagementService.getChannel("channel-1")).thenReturn(channel);

        mockMvc.perform(get("/api/v1/notificator/channels/channel-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("channel-1"))
                .andExpect(jsonPath("$.name").value("Test Channel"))
                .andExpect(jsonPath("$.type").value("EMAIL"))
                .andExpect(jsonPath("$.enabled").value(true));
    }

    @Test
    void testCreateChannel_Success() throws Exception {
        NotificationChannelDTO request = new NotificationChannelDTO();
        request.setName("Test Email Channel");
        request.setType("EMAIL");
        request.setEnabled(true);
        
        EmailChannelConfig config = new EmailChannelConfig();
        config.setSmtpServer("smtp.example.com");
        config.setSmtpPort(587);
        config.setUsername("user@example.com");
        config.setPassword("password");
        config.setFromAddress("noreply@example.com");
        config.setToAddresses(List.of("admin@example.com"));
        request.setConfiguration(Map.of("email", config));

        NotificationChannelDTO response = new NotificationChannelDTO();
        response.setId("channel-123");
        response.setName("Test Email Channel");
        response.setType("EMAIL");
        response.setEnabled(true);
        response.setConfiguration(Map.of("email", config));
        response.setCreatedAt("2024-01-15T10:30:00");
        response.setUpdatedAt("2024-01-15T10:30:00");

        when(channelManagementService.createChannel(any(NotificationChannelDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/notificator/channels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("channel-123"))
                .andExpect(jsonPath("$.name").value("Test Email Channel"))
                .andExpect(jsonPath("$.type").value("EMAIL"))
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void testUpdateChannel_Success() throws Exception {
        NotificationChannelDTO request = new NotificationChannelDTO();
        request.setName("Updated Channel");
        request.setType("EMAIL");
        request.setEnabled(false);
        request.setConfiguration(Map.of());

        NotificationChannelDTO response = new NotificationChannelDTO();
        response.setId("channel-123");
        response.setName("Updated Channel");
        response.setType("EMAIL");
        response.setEnabled(false);
        response.setUpdatedAt("2024-01-15T11:30:00");

        when(channelManagementService.updateChannel(eq("channel-123"), any(NotificationChannelDTO.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/notificator/channels/channel-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("channel-123"))
                .andExpect(jsonPath("$.name").value("Updated Channel"))
                .andExpect(jsonPath("$.type").value("EMAIL"))
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void testUpdateChannel_NotFound() throws Exception {
        NotificationChannelDTO request = new NotificationChannelDTO();
        request.setName("Updated Channel");
        request.setType("EMAIL");
        request.setConfiguration(Map.of());

        when(channelManagementService.updateChannel(eq("nonexistent"), any(NotificationChannelDTO.class)))
                .thenReturn(null);

        mockMvc.perform(put("/api/v1/notificator/channels/nonexistent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteChannel_Success() throws Exception {
        when(channelManagementService.deleteChannel("channel-123")).thenReturn(true);

        mockMvc.perform(delete("/api/v1/notificator/channels/channel-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Channel deleted successfully"));
    }

    @Test
    void testDeleteChannel_NotFound() throws Exception {
        when(channelManagementService.deleteChannel("nonexistent")).thenReturn(false);

        mockMvc.perform(delete("/api/v1/notificator/channels/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateChannel_ValidationError() throws Exception {
        // Test with invalid data (empty name)
        NotificationChannelDTO request = new NotificationChannelDTO();
        request.setName("");
        request.setType("EMAIL");

        mockMvc.perform(post("/api/v1/notificator/channels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}