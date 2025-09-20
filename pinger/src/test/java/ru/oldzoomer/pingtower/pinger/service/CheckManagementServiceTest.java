package ru.oldzoomer.pingtower.pinger.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.oldzoomer.pingtower.pinger.dto.CheckConfigurationDTO;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CheckManagementServiceTest {

    @InjectMocks
    private CheckManagementService checkManagementService;

    private CheckConfigurationDTO testCheck;

    @BeforeEach
    void setUp() {
        testCheck = new CheckConfigurationDTO();
        testCheck.setId("test-check-1");
        testCheck.setType("HTTP");
        testCheck.setResourceUrl("https://example.com");
        testCheck.setFrequency(60000L);
        testCheck.setTimeout(5000);
        testCheck.setExpectedStatusCode(200);
        testCheck.setExpectedResponseTime(1000L);
        testCheck.setValidateSsl(true);
    }

    @Test
    void testCreateCheck_WithId() {
        CheckConfigurationDTO result = checkManagementService.createCheck(testCheck);

        assertNotNull(result);
        assertEquals("test-check-1", result.getId());
        assertEquals("HTTP", result.getType());
        assertEquals("https://example.com", result.getResourceUrl());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void testCreateCheck_WithoutId() {
        testCheck.setId(null);

        CheckConfigurationDTO result = checkManagementService.createCheck(testCheck);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertFalse(result.getId().isEmpty());
        assertEquals("HTTP", result.getType());
        assertEquals("https://example.com", result.getResourceUrl());
    }

    @Test
    void testGetCheck_NotFound() {
        CheckConfigurationDTO result = checkManagementService.getCheck("nonexistent");

        assertNull(result);
    }

    @Test
    void testGetCheck_Found() {
        checkManagementService.createCheck(testCheck);

        CheckConfigurationDTO result = checkManagementService.getCheck("test-check-1");

        assertNotNull(result);
        assertEquals("test-check-1", result.getId());
        assertEquals("HTTP", result.getType());
    }

    @Test
    void testGetChecks_Empty() {
        Map<String, Object> result = checkManagementService.getChecks(1, 20);

        assertNotNull(result);
        assertEquals(0, result.get("total"));
        assertTrue(((java.util.List<?>) result.get("checks")).isEmpty());
        assertEquals(1, result.get("page"));
        assertEquals(20, result.get("size"));
    }

    @Test
    void testGetChecks_WithData() {
        checkManagementService.createCheck(testCheck);

        Map<String, Object> result = checkManagementService.getChecks(1, 20);

        assertNotNull(result);
        assertEquals(1, result.get("total"));
        assertEquals(1, ((java.util.List<?>) result.get("checks")).size());
        assertEquals(1, result.get("page"));
        assertEquals(20, result.get("size"));
    }

    @Test
    void testGetChecks_Pagination() {
        // Create multiple checks
        for (int i = 1; i <= 5; i++) {
            CheckConfigurationDTO check = new CheckConfigurationDTO();
            check.setId("check-" + i);
            check.setType("HTTP");
            check.setResourceUrl("https://example" + i + ".com");
            checkManagementService.createCheck(check);
        }

        Map<String, Object> result = checkManagementService.getChecks(2, 2);

        assertNotNull(result);
        assertEquals(5, result.get("total"));
        assertEquals(2, ((java.util.List<?>) result.get("checks")).size());
        assertEquals(2, result.get("page"));
        assertEquals(2, result.get("size"));
    }

    @Test
    void testUpdateCheck_Success() {
        checkManagementService.createCheck(testCheck);

        CheckConfigurationDTO update = new CheckConfigurationDTO();
        update.setType("HTTPS");
        update.setResourceUrl("https://updated.example.com");
        update.setFrequency(30000L);

        CheckConfigurationDTO result = checkManagementService.updateCheck("test-check-1", update);

        assertNotNull(result);
        assertEquals("test-check-1", result.getId());
        assertEquals("HTTPS", result.getType());
        assertEquals("https://updated.example.com", result.getResourceUrl());
        assertEquals(30000L, result.getFrequency());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void testUpdateCheck_NotFound() {
        CheckConfigurationDTO update = new CheckConfigurationDTO();
        update.setType("HTTPS");

        CheckConfigurationDTO result = checkManagementService.updateCheck("nonexistent", update);

        assertNull(result);
    }

    @Test
    void testDeleteCheck_Success() {
        checkManagementService.createCheck(testCheck);

        boolean result = checkManagementService.deleteCheck("test-check-1");

        assertTrue(result);
        assertNull(checkManagementService.getCheck("test-check-1"));
    }

    @Test
    void testDeleteCheck_NotFound() {
        boolean result = checkManagementService.deleteCheck("nonexistent");

        assertFalse(result);
    }
}