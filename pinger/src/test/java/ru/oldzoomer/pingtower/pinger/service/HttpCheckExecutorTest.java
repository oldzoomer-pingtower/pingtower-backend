package ru.oldzoomer.pingtower.pinger.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.oldzoomer.pingtower.pinger.dto.CheckConfiguration;
import ru.oldzoomer.pingtower.pinger.dto.CheckResult;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HttpCheckExecutorTest {

    @InjectMocks
    private HttpCheckExecutor httpCheckExecutor;

    private CheckConfiguration config;
    private HttpURLConnection mockConnection;

    @BeforeEach
    void setUp() {
        config = new CheckConfiguration();
        config.setId("test-check");
        config.setType("HTTP");
        config.setResourceUrl("http://example.com");
        config.setTimeout(5000);
        config.setExpectedStatusCode(200);
        config.setExpectedResponseTime(1000L);
        config.setValidateSsl(false);
    }

    @Test
    void testExecute_SuccessfulHttpCheck() throws Exception {
        // Используем spy для переопределения метода createURL
        HttpCheckExecutor spyExecutor = spy(httpCheckExecutor);
        
        URL mockUrl = mock(URL.class);
        mockConnection = mock(HttpURLConnection.class);
        
        doReturn(mockUrl).when(spyExecutor).createURL("http://example.com");
        when(mockUrl.openConnection()).thenReturn(mockConnection);
        
        when(mockConnection.getResponseCode()).thenReturn(200);
        when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{1}));
        
        CheckResult result = spyExecutor.execute(config);
        
        assertNotNull(result);
        assertEquals("test-check", result.getCheckId());
        assertEquals("http://example.com", result.getResourceUrl());
        assertEquals("UP", result.getStatus());
        assertEquals(200, result.getHttpStatusCode());
        assertNull(result.getErrorMessage());
        assertTrue(result.getResponseTime() >= 0);
        assertNotNull(result.getTimestamp());
    }

    @Test
    void testExecute_HttpErrorStatus() throws Exception {
        HttpCheckExecutor spyExecutor = spy(httpCheckExecutor);
        
        URL mockUrl = mock(URL.class);
        mockConnection = mock(HttpURLConnection.class);
        
        doReturn(mockUrl).when(spyExecutor).createURL("http://example.com");
        when(mockUrl.openConnection()).thenReturn(mockConnection);
        
        when(mockConnection.getResponseCode()).thenReturn(404);
        when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{1}));
        
        CheckResult result = spyExecutor.execute(config);
        
        assertNotNull(result);
        assertEquals("DOWN", result.getStatus());
        assertEquals(404, result.getHttpStatusCode());
        assertEquals("HTTP error code: 404", result.getErrorMessage());
    }

    @Test
    void testExecute_UnexpectedStatusCode() throws Exception {
        config.setExpectedStatusCode(201);
        
        HttpCheckExecutor spyExecutor = spy(httpCheckExecutor);
        
        URL mockUrl = mock(URL.class);
        mockConnection = mock(HttpURLConnection.class);
        
        doReturn(mockUrl).when(spyExecutor).createURL("http://example.com");
        when(mockUrl.openConnection()).thenReturn(mockConnection);
        
        when(mockConnection.getResponseCode()).thenReturn(200);
        when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{1}));
        
        CheckResult result = spyExecutor.execute(config);
        
        assertNotNull(result);
        assertEquals("DOWN", result.getStatus());
        assertEquals("Expected status code 201, but got 200", result.getErrorMessage());
    }

    @Test
    void testExecute_ConnectionError() throws Exception {
        HttpCheckExecutor spyExecutor = spy(httpCheckExecutor);
        
        URL mockUrl = mock(URL.class);
        
        doReturn(mockUrl).when(spyExecutor).createURL("http://example.com");
        when(mockUrl.openConnection()).thenThrow(new IOException("Connection failed"));
        
        CheckResult result = spyExecutor.execute(config);
        
        assertNotNull(result);
        assertEquals("DOWN", result.getStatus());
        assertEquals("Connection failed", result.getErrorMessage());
    }

    @Test
    void testExecute_HttpsWithSslValidation() throws Exception {
        config.setResourceUrl("https://example.com");
        config.setValidateSsl(true);
        
        HttpCheckExecutor spyExecutor = spy(httpCheckExecutor);
        
        URL mockUrl = mock(URL.class);
        HttpsURLConnection mockHttpsConnection = mock(HttpsURLConnection.class);
        
        doReturn(mockUrl).when(spyExecutor).createURL("https://example.com");
        when(mockUrl.openConnection()).thenReturn(mockHttpsConnection);

        when(mockHttpsConnection.getResponseCode()).thenReturn(200);
        when(mockHttpsConnection.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{1}));
        when(mockHttpsConnection.getURL()).thenReturn(mockUrl); // Добавляем мокирование getURL()

        X509Certificate mockCert = mock(X509Certificate.class);
        when(mockCert.getNotAfter()).thenReturn(Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant()));
        when(mockHttpsConnection.getServerCertificates()).thenReturn(new java.security.cert.Certificate[]{mockCert});
        
        CheckResult result = spyExecutor.execute(config);
        
        assertNotNull(result);
        assertEquals("UP", result.getStatus());
        assertNotNull(result.getMetrics());
        assertTrue(result.getMetrics().getSslValid());
        assertNotNull(result.getMetrics().getSslExpirationDate());
    }

    @Test
    void testSupports() {
        assertTrue(httpCheckExecutor.supports("HTTP"));
        assertTrue(httpCheckExecutor.supports("HTTPS"));
        assertFalse(httpCheckExecutor.supports("TCP"));
        assertFalse(httpCheckExecutor.supports("PING"));
    }
}