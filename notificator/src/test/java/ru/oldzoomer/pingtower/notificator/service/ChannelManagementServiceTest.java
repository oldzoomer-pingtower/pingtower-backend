package ru.oldzoomer.pingtower.notificator.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.oldzoomer.pingtower.notificator.dto.EmailChannelConfig;
import ru.oldzoomer.pingtower.notificator.dto.NotificationChannelDTO;
import ru.oldzoomer.pingtower.notificator.dto.TelegramChannelConfig;
import ru.oldzoomer.pingtower.notificator.dto.WebhookChannelConfig;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ChannelManagementServiceTest {

    private ChannelManagementService channelManagementService;

    @BeforeEach
    void setUp() {
        channelManagementService = new ChannelManagementService();
    }

    @Test
    void testGetChannels_EmptyList() {
        Map<String, Object> result = channelManagementService.getChannels(1, 10, null);
        
        assertEquals(0, result.get("total"));
        assertEquals(1, result.get("page"));
        assertEquals(10, result.get("size"));
        assertTrue(((java.util.List<?>) result.get("channels")).isEmpty());
    }

    @Test
    void testCreateChannel_Email() {
        NotificationChannelDTO channel = new NotificationChannelDTO();
        channel.setName("Test Email Channel");
        channel.setType("EMAIL");
        channel.setEnabled(true);
        
        EmailChannelConfig config = new EmailChannelConfig();
        config.setSmtpServer("smtp.example.com");
        config.setSmtpPort(587);
        config.setUsername("user@example.com");
        config.setPassword("password");
        config.setFromAddress("noreply@example.com");
        config.setToAddresses(java.util.List.of("admin@example.com"));
        channel.setConfiguration(Map.of("email", config));

        NotificationChannelDTO created = channelManagementService.createChannel(channel);
        
        assertNotNull(created.getId());
        assertNotNull(created.getCreatedAt());
        assertNotNull(created.getUpdatedAt());
        assertEquals("Test Email Channel", created.getName());
        assertEquals("EMAIL", created.getType());
        assertTrue(created.isEnabled());
        assertNotNull(created.getConfiguration());
    }

    @Test
    void testCreateChannel_Telegram() {
        NotificationChannelDTO channel = new NotificationChannelDTO();
        channel.setName("Test Telegram Channel");
        channel.setType("TELEGRAM");
        channel.setEnabled(true);
        
        TelegramChannelConfig config = new TelegramChannelConfig();
        config.setBotToken("123456:ABC-DEF1234ghIkl-zyx57W2v1u123ew11");
        config.setChatId("123456789");
        channel.setConfiguration(Map.of("telegram", config));

        NotificationChannelDTO created = channelManagementService.createChannel(channel);
        
        assertNotNull(created.getId());
        assertEquals("Test Telegram Channel", created.getName());
        assertEquals("TELEGRAM", created.getType());
        assertNotNull(created.getConfiguration());
    }

    @Test
    void testCreateChannel_Webhook() {
        NotificationChannelDTO channel = new NotificationChannelDTO();
        channel.setName("Test Webhook Channel");
        channel.setType("WEBHOOK");
        channel.setEnabled(true);
        
        WebhookChannelConfig config = new WebhookChannelConfig();
        config.setUrl("https://webhook.example.com/notifications");
        config.setMethod("POST");
        config.setHeaders(Map.of("Content-Type", "application/json", "Authorization", "Bearer token123"));
        config.setBodyTemplate("{\"message\": \"{{message}}\"}");
        channel.setConfiguration(Map.of("webhook", config));

        NotificationChannelDTO created = channelManagementService.createChannel(channel);
        
        assertNotNull(created.getId());
        assertEquals("Test Webhook Channel", created.getName());
        assertEquals("WEBHOOK", created.getType());
        assertNotNull(created.getConfiguration());
    }

    @Test
    void testGetChannel_NotFound() {
        NotificationChannelDTO result = channelManagementService.getChannel("nonexistent");
        assertNull(result);
    }

    @Test
    void testGetChannel_Found() {
        NotificationChannelDTO channel = new NotificationChannelDTO();
        channel.setName("Test Channel");
        channel.setType("EMAIL");
        NotificationChannelDTO created = channelManagementService.createChannel(channel);
        
        NotificationChannelDTO found = channelManagementService.getChannel(created.getId());
        
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals("Test Channel", found.getName());
        assertEquals("EMAIL", found.getType());
    }

    @Test
    void testUpdateChannel_Success() {
        NotificationChannelDTO channel = new NotificationChannelDTO();
        channel.setName("Original Name");
        channel.setType("EMAIL");
        NotificationChannelDTO created = channelManagementService.createChannel(channel);
        
        NotificationChannelDTO update = new NotificationChannelDTO();
        update.setName("Updated Name");
        update.setType("EMAIL");
        update.setEnabled(false);
        
        NotificationChannelDTO updated = channelManagementService.updateChannel(created.getId(), update);
        
        assertNotNull(updated);
        assertEquals(created.getId(), updated.getId());
        assertEquals("Updated Name", updated.getName());
        assertEquals("EMAIL", updated.getType());
        assertFalse(updated.isEnabled());
        assertNotNull(updated.getUpdatedAt());
    }

    @Test
    void testUpdateChannel_NotFound() {
        NotificationChannelDTO update = new NotificationChannelDTO();
        update.setName("Test");
        update.setType("EMAIL");
        
        NotificationChannelDTO result = channelManagementService.updateChannel("nonexistent", update);
        assertNull(result);
    }

    @Test
    void testDeleteChannel_Success() {
        NotificationChannelDTO channel = new NotificationChannelDTO();
        channel.setName("Test Channel");
        channel.setType("EMAIL");
        NotificationChannelDTO created = channelManagementService.createChannel(channel);
        
        boolean deleted = channelManagementService.deleteChannel(created.getId());
        assertTrue(deleted);
        
        NotificationChannelDTO found = channelManagementService.getChannel(created.getId());
        assertNull(found);
    }

    @Test
    void testDeleteChannel_NotFound() {
        boolean deleted = channelManagementService.deleteChannel("nonexistent");
        assertFalse(deleted);
    }

    @Test
    void testGetChannels_WithPagination() {
        // Create multiple channels
        for (int i = 1; i <= 15; i++) {
            NotificationChannelDTO channel = new NotificationChannelDTO();
            channel.setName("Channel " + i);
            channel.setType(i % 2 == 0 ? "EMAIL" : "TELEGRAM");
            channelManagementService.createChannel(channel);
        }
        
        // Test first page
        Map<String, Object> page1 = channelManagementService.getChannels(1, 5, null);
        assertEquals(15, page1.get("total"));
        assertEquals(1, page1.get("page"));
        assertEquals(5, page1.get("size"));
        assertEquals(5, ((java.util.List<?>) page1.get("channels")).size());
        
        // Test second page
        Map<String, Object> page2 = channelManagementService.getChannels(2, 5, null);
        assertEquals(15, page2.get("total"));
        assertEquals(2, page2.get("page"));
        assertEquals(5, page2.get("size"));
        assertEquals(5, ((java.util.List<?>) page2.get("channels")).size());
        
        // Test third page (should have 5 items)
        Map<String, Object> page3 = channelManagementService.getChannels(3, 5, null);
        assertEquals(15, page3.get("total"));
        assertEquals(3, page3.get("page"));
        assertEquals(5, page3.get("size"));
        assertEquals(5, ((java.util.List<?>) page3.get("channels")).size());
    }

    @Test
    void testGetChannels_WithTypeFilter() {
        // Create channels of different types
        NotificationChannelDTO emailChannel = new NotificationChannelDTO();
        emailChannel.setName("Email Channel");
        emailChannel.setType("EMAIL");
        channelManagementService.createChannel(emailChannel);
        
        NotificationChannelDTO telegramChannel = new NotificationChannelDTO();
        telegramChannel.setName("Telegram Channel");
        telegramChannel.setType("TELEGRAM");
        channelManagementService.createChannel(telegramChannel);
        
        NotificationChannelDTO webhookChannel = new NotificationChannelDTO();
        webhookChannel.setName("Webhook Channel");
        webhookChannel.setType("WEBHOOK");
        channelManagementService.createChannel(webhookChannel);
        
        // Filter by EMAIL
        Map<String, Object> emailResult = channelManagementService.getChannels(1, 10, "EMAIL");
        assertEquals(1, emailResult.get("total"));
        assertEquals(1, ((java.util.List<?>) emailResult.get("channels")).size());
        
        // Filter by TELEGRAM
        Map<String, Object> telegramResult = channelManagementService.getChannels(1, 10, "TELEGRAM");
        assertEquals(1, telegramResult.get("total"));
        assertEquals(1, ((java.util.List<?>) telegramResult.get("channels")).size());
        
        // Filter by WEBHOOK
        Map<String, Object> webhookResult = channelManagementService.getChannels(1, 10, "WEBHOOK");
        assertEquals(1, webhookResult.get("total"));
        assertEquals(1, ((java.util.List<?>) webhookResult.get("channels")).size());
        
        // Filter by nonexistent type
        Map<String, Object> noneResult = channelManagementService.getChannels(1, 10, "NONE");
        assertEquals(0, noneResult.get("total"));
        assertEquals(0, ((java.util.List<?>) noneResult.get("channels")).size());
    }
}