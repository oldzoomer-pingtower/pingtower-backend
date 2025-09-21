package ru.oldzoomer.pingtower.pinger.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckExecutorFactoryTest {

    @Mock
    private HttpCheckExecutor httpCheckExecutor;

    @Mock
    private TcpCheckExecutor tcpCheckExecutor;

    @Mock
    private DnsCheckExecutor dnsCheckExecutor;

    @InjectMocks
    private CheckExecutorFactory checkExecutorFactory;

    @BeforeEach
    void setUp() {
        // Initialize the factory with mocked executors
        checkExecutorFactory = new CheckExecutorFactory(List.of(
                httpCheckExecutor,
                tcpCheckExecutor,
                dnsCheckExecutor
        ));
    }

    @Test
    void testGetExecutor_HttpType() {
        when(httpCheckExecutor.supports("HTTP")).thenReturn(true);
        
        CheckExecutor executor = checkExecutorFactory.getExecutor("HTTP");
        
        assertNotNull(executor);
        assertEquals(httpCheckExecutor, executor);
        verify(httpCheckExecutor).supports("HTTP");
    }

    @Test
    void testGetExecutor_HttpsType() {
        when(httpCheckExecutor.supports("HTTPS")).thenReturn(true);
        
        CheckExecutor executor = checkExecutorFactory.getExecutor("HTTPS");
        
        assertNotNull(executor);
        assertEquals(httpCheckExecutor, executor);
        verify(httpCheckExecutor).supports("HTTPS");
    }

    @Test
    void testGetExecutor_TcpType() {
        when(tcpCheckExecutor.supports("TCP")).thenReturn(true);
        
        CheckExecutor executor = checkExecutorFactory.getExecutor("TCP");
        
        assertNotNull(executor);
        assertEquals(tcpCheckExecutor, executor);
        verify(tcpCheckExecutor).supports("TCP");
    }

    @Test
    void testGetExecutor_DnsType() {
        when(dnsCheckExecutor.supports("DNS")).thenReturn(true);
        
        CheckExecutor executor = checkExecutorFactory.getExecutor("DNS");
        
        assertNotNull(executor);
        assertEquals(dnsCheckExecutor, executor);
        verify(dnsCheckExecutor).supports("DNS");
    }

    @Test
    void testGetExecutor_CaseInsensitive() {
        when(httpCheckExecutor.supports("http")).thenReturn(true);
        
        CheckExecutor executor = checkExecutorFactory.getExecutor("http");
        
        assertNotNull(executor);
        assertEquals(httpCheckExecutor, executor);
        verify(httpCheckExecutor).supports("http");
    }

    @Test
    void testGetExecutor_UnknownType() {
        when(httpCheckExecutor.supports("UNKNOWN")).thenReturn(false);
        when(tcpCheckExecutor.supports("UNKNOWN")).thenReturn(false);
        when(dnsCheckExecutor.supports("UNKNOWN")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> checkExecutorFactory.getExecutor("UNKNOWN"));
    }

    @Test
    void testGetExecutor_NullType() {
        assertThrows(IllegalArgumentException.class, () -> checkExecutorFactory.getExecutor(null));
    }

    @Test
    void testGetExecutor_EmptyType() {
        assertThrows(IllegalArgumentException.class, () -> checkExecutorFactory.getExecutor(""));
    }

    @Test
    void testGetExecutor_WhitespaceType() {
        assertThrows(IllegalArgumentException.class, () -> checkExecutorFactory.getExecutor("   "));
    }

}