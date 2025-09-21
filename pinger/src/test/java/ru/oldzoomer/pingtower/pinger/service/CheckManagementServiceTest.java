package ru.oldzoomer.pingtower.pinger.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.oldzoomer.pingtower.pinger.dto.CheckConfigurationDTO;
import ru.oldzoomer.pingtower.pinger.entity.CheckConfigurationEntity;
import ru.oldzoomer.pingtower.pinger.mapper.CheckConfigurationMapper;
import ru.oldzoomer.pingtower.pinger.repository.CheckConfigurationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckManagementServiceTest {

    @Mock
    private CheckConfigurationRepository checkConfigurationRepository;

    @Mock
    private CheckConfigurationMapper checkConfigurationMapper;

    @InjectMocks
    private CheckManagementService checkManagementService;

    private CheckConfigurationDTO testCheckDTO;
    private CheckConfigurationEntity testCheckEntity;

    @BeforeEach
    void setUp() {
        testCheckDTO = new CheckConfigurationDTO();
        testCheckDTO.setId("test-check-1");
        testCheckDTO.setType("HTTP");
        testCheckDTO.setResourceUrl("https://example.com");
        testCheckDTO.setFrequency(60000L);
        testCheckDTO.setTimeout(5000);
        testCheckDTO.setExpectedStatusCode(200);
        testCheckDTO.setExpectedResponseTime(1000L);
        testCheckDTO.setValidateSsl(true);

        testCheckEntity = new CheckConfigurationEntity();
        testCheckEntity.setId("test-check-1");
        testCheckEntity.setType("HTTP");
        testCheckEntity.setResourceUrl("https://example.com");
        testCheckEntity.setFrequency(60000L);
        testCheckEntity.setTimeout(5000);
        testCheckEntity.setExpectedStatusCode(200);
        testCheckEntity.setExpectedResponseTime(1000L);
        testCheckEntity.setValidateSsl(true);
        testCheckEntity.setCreatedAt(LocalDateTime.now());
        testCheckEntity.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateCheck_WithId() {
        // Setup the DTO with timestamps
        CheckConfigurationDTO testCheckDTOWithTimestamps = new CheckConfigurationDTO();
        testCheckDTOWithTimestamps.setId("test-check-1");
        testCheckDTOWithTimestamps.setType("HTTP");
        testCheckDTOWithTimestamps.setResourceUrl("https://example.com");
        testCheckDTOWithTimestamps.setFrequency(60000L);
        testCheckDTOWithTimestamps.setTimeout(5000);
        testCheckDTOWithTimestamps.setExpectedStatusCode(200);
        testCheckDTOWithTimestamps.setExpectedResponseTime(1000L);
        testCheckDTOWithTimestamps.setValidateSsl(true);
        testCheckDTOWithTimestamps.setCreatedAt(LocalDateTime.now().toString());
        testCheckDTOWithTimestamps.setUpdatedAt(LocalDateTime.now().toString());
        
        // Setup
        when(checkConfigurationMapper.toEntity(testCheckDTO)).thenReturn(testCheckEntity);
        when(checkConfigurationRepository.save(testCheckEntity)).thenReturn(testCheckEntity);
        when(checkConfigurationMapper.toDto(testCheckEntity)).thenReturn(testCheckDTOWithTimestamps);

        CheckConfigurationDTO result = checkManagementService.createCheck(testCheckDTO);

        assertNotNull(result);
        assertEquals("test-check-1", result.getId());
        assertEquals("HTTP", result.getType());
        assertEquals("https://example.com", result.getResourceUrl());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        verify(checkConfigurationMapper).toEntity(testCheckDTO);
        verify(checkConfigurationRepository).save(testCheckEntity);
        verify(checkConfigurationMapper).toDto(testCheckEntity);
    }

    @Test
    void testCreateCheck_WithoutId() {
        CheckConfigurationDTO checkWithoutId = new CheckConfigurationDTO();
        checkWithoutId.setId(null);
        checkWithoutId.setType("HTTP");
        checkWithoutId.setResourceUrl("https://example.com");

        CheckConfigurationEntity entityWithoutId = new CheckConfigurationEntity();
        entityWithoutId.setId("generated-id");
        entityWithoutId.setType("HTTP");
        entityWithoutId.setResourceUrl("https://example.com");
        entityWithoutId.setCreatedAt(LocalDateTime.now());
        entityWithoutId.setUpdatedAt(LocalDateTime.now());

        CheckConfigurationDTO dtoWithId = new CheckConfigurationDTO();
        dtoWithId.setId("generated-id");
        dtoWithId.setType("HTTP");
        dtoWithId.setResourceUrl("https://example.com");
        dtoWithId.setCreatedAt(LocalDateTime.now().toString());
        dtoWithId.setUpdatedAt(LocalDateTime.now().toString());

        when(checkConfigurationMapper.toEntity(checkWithoutId)).thenReturn(entityWithoutId);
        when(checkConfigurationRepository.save(entityWithoutId)).thenReturn(entityWithoutId);
        when(checkConfigurationMapper.toDto(entityWithoutId)).thenReturn(dtoWithId);

        CheckConfigurationDTO result = checkManagementService.createCheck(checkWithoutId);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertFalse(result.getId().isEmpty());
        assertEquals("HTTP", result.getType());
        assertEquals("https://example.com", result.getResourceUrl());
    }

    @Test
    void testGetCheck_NotFound() {
        when(checkConfigurationRepository.findById("nonexistent")).thenReturn(Optional.empty());

        CheckConfigurationDTO result = checkManagementService.getCheck("nonexistent");

        assertNull(result);
    }

    @Test
    void testGetCheck_Found() {
        when(checkConfigurationRepository.findById("test-check-1")).thenReturn(Optional.of(testCheckEntity));
        when(checkConfigurationMapper.toDto(testCheckEntity)).thenReturn(testCheckDTO);

        CheckConfigurationDTO result = checkManagementService.getCheck("test-check-1");

        assertNotNull(result);
        assertEquals("test-check-1", result.getId());
        assertEquals("HTTP", result.getType());

        verify(checkConfigurationRepository).findById("test-check-1");
        verify(checkConfigurationMapper).toDto(testCheckEntity);
    }

    @Test
    void testGetChecks_Empty() {
        org.springframework.data.domain.Page<CheckConfigurationEntity> emptyPage = 
            new org.springframework.data.domain.PageImpl<>(List.of());
        
        when(checkConfigurationRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(emptyPage);

        Map<String, Object> result = checkManagementService.getChecks(1, 20);

        assertNotNull(result);
        assertEquals(0L, result.get("total"));
        assertTrue(((java.util.List<?>) result.get("checks")).isEmpty());
        assertEquals(1, result.get("page"));
        assertEquals(20, result.get("size"));
    }

    @Test
    void testGetChecks_WithData() {
        org.springframework.data.domain.Page<CheckConfigurationEntity> pageWithOneItem = 
            new org.springframework.data.domain.PageImpl<>(List.of(testCheckEntity));
        
        when(checkConfigurationRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(pageWithOneItem);
        when(checkConfigurationMapper.toDto(testCheckEntity)).thenReturn(testCheckDTO);

        Map<String, Object> result = checkManagementService.getChecks(1, 20);

        assertNotNull(result);
        assertEquals(1L, result.get("total"));
        assertEquals(1, ((java.util.List<?>) result.get("checks")).size());
        assertEquals(1, result.get("page"));
        assertEquals(20, result.get("size"));
    }

    @Test
    void testGetChecks_Pagination() {
        CheckConfigurationEntity check1 = new CheckConfigurationEntity();
        check1.setId("check-1");
        check1.setType("HTTP");
        check1.setResourceUrl("https://example1.com");
        
        CheckConfigurationEntity check2 = new CheckConfigurationEntity();
        check2.setId("check-2");
        check2.setType("HTTP");
        check2.setResourceUrl("https://example2.com");
        
        CheckConfigurationDTO check1DTO = new CheckConfigurationDTO();
        check1DTO.setId("check-1");
        check1DTO.setType("HTTP");
        check1DTO.setResourceUrl("https://example1.com");
        
        CheckConfigurationDTO check2DTO = new CheckConfigurationDTO();
        check2DTO.setId("check-2");
        check2DTO.setType("HTTP");
        check2DTO.setResourceUrl("https://example2.com");

        org.springframework.data.domain.Page<CheckConfigurationEntity> page = 
            new org.springframework.data.domain.PageImpl<>(List.of(check1, check2));
        
        when(checkConfigurationRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(page);
        when(checkConfigurationMapper.toDto(check1)).thenReturn(check1DTO);
        when(checkConfigurationMapper.toDto(check2)).thenReturn(check2DTO);

        Map<String, Object> result = checkManagementService.getChecks(2, 2);

        assertNotNull(result);
        assertEquals(2L, result.get("total"));
        assertEquals(2, ((java.util.List<?>) result.get("checks")).size());
        assertEquals(2, result.get("page"));
        assertEquals(2, result.get("size"));
    }

    @Test
    void testUpdateCheck_Success() {
        // Create the updated entity
        CheckConfigurationEntity updatedEntity = new CheckConfigurationEntity();
        updatedEntity.setId("test-check-1");
        updatedEntity.setType("HTTPS");
        updatedEntity.setResourceUrl("https://updated.example.com");
        updatedEntity.setFrequency(30000L);
        updatedEntity.setTimeout(5000);
        updatedEntity.setExpectedStatusCode(200);
        updatedEntity.setExpectedResponseTime(1000L);
        updatedEntity.setValidateSsl(true);
        updatedEntity.setCreatedAt(testCheckEntity.getCreatedAt()); // Keep original createdAt
        updatedEntity.setUpdatedAt(LocalDateTime.now());
        
        // Create the update DTO
        CheckConfigurationDTO updateDTO = new CheckConfigurationDTO();
        updateDTO.setType("HTTPS");
        updateDTO.setResourceUrl("https://updated.example.com");
        updateDTO.setFrequency(30000L);
        
        // Create the updated DTO for return
        CheckConfigurationDTO updatedDTO = new CheckConfigurationDTO();
        updatedDTO.setId("test-check-1");
        updatedDTO.setType("HTTPS");
        updatedDTO.setResourceUrl("https://updated.example.com");
        updatedDTO.setFrequency(30000L);
        updatedDTO.setTimeout(5000);
        updatedDTO.setExpectedStatusCode(200);
        updatedDTO.setExpectedResponseTime(1000L);
        updatedDTO.setValidateSsl(true);
        updatedDTO.setCreatedAt(testCheckDTO.getCreatedAt());
        updatedDTO.setUpdatedAt(LocalDateTime.now().toString());

        when(checkConfigurationRepository.findById("test-check-1")).thenReturn(Optional.of(testCheckEntity));
        when(checkConfigurationMapper.toEntity(updateDTO)).thenReturn(updatedEntity);
        when(checkConfigurationRepository.save(updatedEntity)).thenReturn(updatedEntity);
        when(checkConfigurationMapper.toDto(updatedEntity)).thenReturn(updatedDTO);

        CheckConfigurationDTO result = checkManagementService.updateCheck("test-check-1", updateDTO);

        assertNotNull(result);
        assertEquals("test-check-1", result.getId());
        assertEquals("HTTPS", result.getType());
        assertEquals("https://updated.example.com", result.getResourceUrl());
        assertEquals(30000L, result.getFrequency());
        assertNotNull(result.getUpdatedAt());

        verify(checkConfigurationRepository).findById("test-check-1");
        verify(checkConfigurationMapper).toEntity(updateDTO);
        verify(checkConfigurationRepository).save(updatedEntity);
        verify(checkConfigurationMapper).toDto(updatedEntity);
    }

    @Test
    void testUpdateCheck_NotFound() {
        CheckConfigurationDTO update = new CheckConfigurationDTO();
        update.setType("HTTPS");

        when(checkConfigurationRepository.findById("nonexistent")).thenReturn(Optional.empty());

        CheckConfigurationDTO result = checkManagementService.updateCheck("nonexistent", update);

        assertNull(result);

        verify(checkConfigurationRepository).findById("nonexistent");
        verify(checkConfigurationRepository, never()).save(any(CheckConfigurationEntity.class));
    }

    @Test
    void testDeleteCheck_Success() {
        when(checkConfigurationRepository.existsById("test-check-1")).thenReturn(true);

        boolean result = checkManagementService.deleteCheck("test-check-1");

        assertTrue(result);
        verify(checkConfigurationRepository).existsById("test-check-1");
        verify(checkConfigurationRepository).deleteById("test-check-1");
    }

    @Test
    void testDeleteCheck_NotFound() {
        when(checkConfigurationRepository.existsById("nonexistent")).thenReturn(false);

        boolean result = checkManagementService.deleteCheck("nonexistent");

        assertFalse(result);
        verify(checkConfigurationRepository).existsById("nonexistent");
        verify(checkConfigurationRepository, never()).deleteById("nonexistent");
    }
}