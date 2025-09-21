package ru.oldzoomer.pingtower.notificator.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.oldzoomer.pingtower.notificator.dto.EmailChannelConfig;
import ru.oldzoomer.pingtower.notificator.dto.NotificationChannelDTO;
import ru.oldzoomer.pingtower.notificator.dto.TelegramChannelConfig;
import ru.oldzoomer.pingtower.notificator.dto.WebhookChannelConfig;
import ru.oldzoomer.pingtower.notificator.entity.NotificationChannelEntity;
import ru.oldzoomer.pingtower.notificator.mapper.NotificationChannelMapper;
import ru.oldzoomer.pingtower.notificator.repository.NotificationChannelRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChannelManagementServiceTest {

    @Mock
    private NotificationChannelRepository notificationChannelRepository;

    @Mock
    private NotificationChannelMapper notificationChannelMapper;

    @InjectMocks
    private ChannelManagementService channelManagementService;

    @BeforeEach
    void setUp() {
        // Setup is handled by @InjectMocks
    }

    @Test
    void testGetChannels_EmptyList() {
        Page<NotificationChannelEntity> emptyPage = new PageImpl<>(List.of());
        when(notificationChannelRepository.findAll(any(PageRequest.class))).thenReturn(emptyPage);

        Map<String, Object> result = channelManagementService.getChannels(1, 10, null);
        
        assertEquals(0L, result.get("total"));
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

        NotificationChannelEntity entity = new NotificationChannelEntity();
        entity.setId("test-id");
        entity.setName("Test Email Channel");
        entity.setType("EMAIL");
        entity.setEnabled(true);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        NotificationChannelDTO expectedDto = new NotificationChannelDTO();
        expectedDto.setId("test-id");
        expectedDto.setName("Test Email Channel");
        expectedDto.setType("EMAIL");
        expectedDto.setEnabled(true);
        expectedDto.setConfiguration(Map.of("email", config));
        expectedDto.setCreatedAt(entity.getCreatedAt().toString());
        expectedDto.setUpdatedAt(entity.getUpdatedAt().toString());

        when(notificationChannelMapper.toEntity(channel)).thenReturn(entity);
        when(notificationChannelRepository.save(entity)).thenReturn(entity);
        when(notificationChannelMapper.toDto(entity)).thenReturn(expectedDto);

        NotificationChannelDTO created = channelManagementService.createChannel(channel);
        
        assertNotNull(created.getId());
        assertNotNull(created.getCreatedAt());
        assertNotNull(created.getUpdatedAt());
        assertEquals("Test Email Channel", created.getName());
        assertEquals("EMAIL", created.getType());
        assertTrue(created.isEnabled());
        assertNotNull(created.getConfiguration());
        
        verify(notificationChannelMapper).toEntity(channel);
        verify(notificationChannelRepository).save(entity);
        verify(notificationChannelMapper).toDto(entity);
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

        NotificationChannelEntity entity = new NotificationChannelEntity();
        entity.setId("test-id");
        entity.setName("Test Telegram Channel");
        entity.setType("TELEGRAM");
        entity.setEnabled(true);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        NotificationChannelDTO expectedDto = new NotificationChannelDTO();
        expectedDto.setId("test-id");
        expectedDto.setName("Test Telegram Channel");
        expectedDto.setType("TELEGRAM");
        expectedDto.setEnabled(true);
        expectedDto.setConfiguration(Map.of("telegram", config));
        expectedDto.setCreatedAt(entity.getCreatedAt().toString());
        expectedDto.setUpdatedAt(entity.getUpdatedAt().toString());

        when(notificationChannelMapper.toEntity(channel)).thenReturn(entity);
        when(notificationChannelRepository.save(entity)).thenReturn(entity);
        when(notificationChannelMapper.toDto(entity)).thenReturn(expectedDto);

        NotificationChannelDTO created = channelManagementService.createChannel(channel);
        
        assertNotNull(created.getId());
        assertNotNull(created.getCreatedAt());
        assertNotNull(created.getUpdatedAt());
        assertEquals("Test Telegram Channel", created.getName());
        assertEquals("TELEGRAM", created.getType());
        assertTrue(created.isEnabled());
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

        NotificationChannelEntity entity = new NotificationChannelEntity();
        entity.setId("test-id");
        entity.setName("Test Webhook Channel");
        entity.setType("WEBHOOK");
        entity.setEnabled(true);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        NotificationChannelDTO expectedDto = new NotificationChannelDTO();
        expectedDto.setId("test-id");
        expectedDto.setName("Test Webhook Channel");
        expectedDto.setType("WEBHOOK");
        expectedDto.setEnabled(true);
        expectedDto.setConfiguration(Map.of("webhook", config));
        expectedDto.setCreatedAt(entity.getCreatedAt().toString());
        expectedDto.setUpdatedAt(entity.getUpdatedAt().toString());

        when(notificationChannelMapper.toEntity(channel)).thenReturn(entity);
        when(notificationChannelRepository.save(entity)).thenReturn(entity);
        when(notificationChannelMapper.toDto(entity)).thenReturn(expectedDto);

        NotificationChannelDTO created = channelManagementService.createChannel(channel);
        
        assertNotNull(created.getId());
        assertNotNull(created.getCreatedAt());
        assertNotNull(created.getUpdatedAt());
        assertEquals("Test Webhook Channel", created.getName());
        assertEquals("WEBHOOK", created.getType());
        assertTrue(created.isEnabled());
        assertNotNull(created.getConfiguration());
    }

    @Test
    void testGetChannel_NotFound() {
        when(notificationChannelRepository.findById("nonexistent")).thenReturn(Optional.empty());

        NotificationChannelDTO result = channelManagementService.getChannel("nonexistent");
        assertNull(result);
    }

    @Test
    void testGetChannel_Found() {
        NotificationChannelEntity entity = new NotificationChannelEntity();
        entity.setId("test-id");
        entity.setName("Test Channel");
        entity.setType("EMAIL");
        
        NotificationChannelDTO dto = new NotificationChannelDTO();
        dto.setId("test-id");
        dto.setName("Test Channel");
        dto.setType("EMAIL");

        when(notificationChannelRepository.findById("test-id")).thenReturn(Optional.of(entity));
        when(notificationChannelMapper.toDto(entity)).thenReturn(dto);

        NotificationChannelDTO found = channelManagementService.getChannel("test-id");
        
        assertNotNull(found);
        assertEquals("test-id", found.getId());
        assertEquals("Test Channel", found.getName());
        assertEquals("EMAIL", found.getType());
    }

    @Test
    void testUpdateChannel_Success() {
        NotificationChannelEntity existingEntity = new NotificationChannelEntity();
        existingEntity.setId("test-id");
        existingEntity.setName("Original Name");
        existingEntity.setType("EMAIL");
        existingEntity.setCreatedAt(LocalDateTime.now().minusDays(1));
        
        NotificationChannelEntity updatedEntity = new NotificationChannelEntity();
        updatedEntity.setId("test-id");
        updatedEntity.setName("Updated Name");
        updatedEntity.setType("EMAIL");
        updatedEntity.setEnabled(false);
        updatedEntity.setCreatedAt(existingEntity.getCreatedAt());
        updatedEntity.setUpdatedAt(LocalDateTime.now());
        
        NotificationChannelDTO updateDto = new NotificationChannelDTO();
        updateDto.setName("Updated Name");
        updateDto.setType("EMAIL");
        updateDto.setEnabled(false);
        
        NotificationChannelDTO resultDto = new NotificationChannelDTO();
        resultDto.setId("test-id");
        resultDto.setName("Updated Name");
        resultDto.setType("EMAIL");
        resultDto.setEnabled(false);
        resultDto.setUpdatedAt(LocalDateTime.now().toString());

        when(notificationChannelRepository.findById("test-id")).thenReturn(Optional.of(existingEntity));
        when(notificationChannelMapper.toEntity(updateDto)).thenReturn(updatedEntity);
        when(notificationChannelRepository.save(updatedEntity)).thenReturn(updatedEntity);
        when(notificationChannelMapper.toDto(updatedEntity)).thenReturn(resultDto);

        NotificationChannelDTO updated = channelManagementService.updateChannel("test-id", updateDto);
        
        assertNotNull(updated);
        assertEquals("test-id", updated.getId());
        assertEquals("Updated Name", updated.getName());
        assertEquals("EMAIL", updated.getType());
        assertFalse(updated.isEnabled());
        assertNotNull(updated.getUpdatedAt());
        
        verify(notificationChannelRepository).findById("test-id");
        verify(notificationChannelMapper).toEntity(updateDto);
        verify(notificationChannelRepository).save(updatedEntity);
        verify(notificationChannelMapper).toDto(updatedEntity);
    }

    @Test
    void testUpdateChannel_NotFound() {
        NotificationChannelDTO update = new NotificationChannelDTO();
        update.setName("Test");
        update.setType("EMAIL");
        
        when(notificationChannelRepository.findById("nonexistent")).thenReturn(Optional.empty());

        NotificationChannelDTO result = channelManagementService.updateChannel("nonexistent", update);
        assertNull(result);
        
        verify(notificationChannelRepository).findById("nonexistent");
        verify(notificationChannelRepository, never()).save(any());
    }

    @Test
    void testDeleteChannel_Success() {
        when(notificationChannelRepository.existsById("test-id")).thenReturn(true);

        boolean deleted = channelManagementService.deleteChannel("test-id");
        assertTrue(deleted);
        
        verify(notificationChannelRepository).existsById("test-id");
        verify(notificationChannelRepository).deleteById("test-id");
    }

    @Test
    void testDeleteChannel_NotFound() {
        when(notificationChannelRepository.existsById("nonexistent")).thenReturn(false);

        boolean deleted = channelManagementService.deleteChannel("nonexistent");
        assertFalse(deleted);
        
        verify(notificationChannelRepository).existsById("nonexistent");
        verify(notificationChannelRepository, never()).deleteById("nonexistent");
    }

    @Test
    void testGetChannels_WithPagination() {
        // Create test entities
        List<NotificationChannelEntity> entities = List.of(
            createTestEntity("1", "Channel 1", "EMAIL"),
            createTestEntity("2", "Channel 2", "TELEGRAM"),
            createTestEntity("3", "Channel 3", "EMAIL"),
            createTestEntity("4", "Channel 4", "TELEGRAM"),
            createTestEntity("5", "Channel 5", "WEBHOOK")
        );
        
        Page<NotificationChannelEntity> page = new PageImpl<>(entities);
        
        NotificationChannelDTO dto1 = createTestDto("1", "Channel 1", "EMAIL");
        NotificationChannelDTO dto2 = createTestDto("2", "Channel 2", "TELEGRAM");
        NotificationChannelDTO dto3 = createTestDto("3", "Channel 3", "EMAIL");
        NotificationChannelDTO dto4 = createTestDto("4", "Channel 4", "TELEGRAM");
        NotificationChannelDTO dto5 = createTestDto("5", "Channel 5", "WEBHOOK");
        
        List<NotificationChannelDTO> dtos = List.of(dto1, dto2, dto3, dto4, dto5);

        when(notificationChannelRepository.findAll(any(PageRequest.class))).thenReturn(page);
        when(notificationChannelMapper.toDto(entities.get(0))).thenReturn(dto1);
        when(notificationChannelMapper.toDto(entities.get(1))).thenReturn(dto2);
        when(notificationChannelMapper.toDto(entities.get(2))).thenReturn(dto3);
        when(notificationChannelMapper.toDto(entities.get(3))).thenReturn(dto4);
        when(notificationChannelMapper.toDto(entities.get(4))).thenReturn(dto5);

        // Test first page
        Map<String, Object> page1 = channelManagementService.getChannels(1, 5, null);
        assertEquals(5L, page1.get("total"));
        assertEquals(1, page1.get("page"));
        assertEquals(5, page1.get("size"));
        assertEquals(5, ((java.util.List<?>) page1.get("channels")).size());
    }

    @Test
    void testGetChannels_WithTypeFilter() {
        // Create test entities
        NotificationChannelEntity emailEntity = createTestEntity("1", "Email Channel", "EMAIL");
        NotificationChannelEntity telegramEntity = createTestEntity("2", "Telegram Channel", "TELEGRAM");
        NotificationChannelEntity webhookEntity = createTestEntity("3", "Webhook Channel", "WEBHOOK");
        
        Page<NotificationChannelEntity> emailPage = new PageImpl<>(List.of(emailEntity));
        Page<NotificationChannelEntity> telegramPage = new PageImpl<>(List.of(telegramEntity));
        Page<NotificationChannelEntity> webhookPage = new PageImpl<>(List.of(webhookEntity));
        Page<NotificationChannelEntity> emptyPage = new PageImpl<>(List.of());

        NotificationChannelDTO emailDto = createTestDto("1", "Email Channel", "EMAIL");
        NotificationChannelDTO telegramDto = createTestDto("2", "Telegram Channel", "TELEGRAM");
        NotificationChannelDTO webhookDto = createTestDto("3", "Webhook Channel", "WEBHOOK");

        when(notificationChannelRepository.findByType("EMAIL", PageRequest.of(0, 10))).thenReturn(emailPage);
        when(notificationChannelRepository.findByType("TELEGRAM", PageRequest.of(0, 10))).thenReturn(telegramPage);
        when(notificationChannelRepository.findByType("WEBHOOK", PageRequest.of(0, 10))).thenReturn(webhookPage);
        when(notificationChannelRepository.findByType("NONE", PageRequest.of(0, 10))).thenReturn(emptyPage);
        
        when(notificationChannelMapper.toDto(emailEntity)).thenReturn(emailDto);
        when(notificationChannelMapper.toDto(telegramEntity)).thenReturn(telegramDto);
        when(notificationChannelMapper.toDto(webhookEntity)).thenReturn(webhookDto);

        // Filter by EMAIL
        Map<String, Object> emailResult = channelManagementService.getChannels(1, 10, "EMAIL");
        assertEquals(1L, emailResult.get("total"));
        assertEquals(1, ((java.util.List<?>) emailResult.get("channels")).size());
        
        // Filter by TELEGRAM
        Map<String, Object> telegramResult = channelManagementService.getChannels(1, 10, "TELEGRAM");
        assertEquals(1L, telegramResult.get("total"));
        assertEquals(1, ((java.util.List<?>) telegramResult.get("channels")).size());
        
        // Filter by WEBHOOK
        Map<String, Object> webhookResult = channelManagementService.getChannels(1, 10, "WEBHOOK");
        assertEquals(1L, webhookResult.get("total"));
        assertEquals(1, ((java.util.List<?>) webhookResult.get("channels")).size());
        
        // Filter by nonexistent type
        Map<String, Object> noneResult = channelManagementService.getChannels(1, 10, "NONE");
        assertEquals(0L, noneResult.get("total"));
        assertEquals(0, ((java.util.List<?>) noneResult.get("channels")).size());
    }
    
    private NotificationChannelEntity createTestEntity(String id, String name, String type) {
        NotificationChannelEntity entity = new NotificationChannelEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setType(type);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }
    
    private NotificationChannelDTO createTestDto(String id, String name, String type) {
        NotificationChannelDTO dto = new NotificationChannelDTO();
        dto.setId(id);
        dto.setName(name);
        dto.setType(type);
        dto.setCreatedAt(LocalDateTime.now().toString());
        dto.setUpdatedAt(LocalDateTime.now().toString());
        return dto;
    }
}