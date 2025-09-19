package ru.oldzoomer.pingtower.pinger.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.oldzoomer.pingtower.pinger.dto.CheckConfiguration;
import ru.oldzoomer.pingtower.pinger.dto.CheckResult;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalDateTime;

@Slf4j
@Service
public class TcpCheckExecutor implements CheckExecutor {
    
    @Override
    public CheckResult execute(CheckConfiguration config) {
        CheckResult result = new CheckResult();
        result.setCheckId(config.getId());
        result.setResourceUrl(config.getResourceUrl());
        result.setTimestamp(LocalDateTime.now());
        
        long startTime = System.currentTimeMillis();
        long connectionTime = 0;
        
        try {
            // Разбор URL для получения хоста и порта
            String[] parts = config.getResourceUrl().replace("tcp://", "").split(":");
            String host = parts[0];
            int port = Integer.parseInt(parts[1]);
            
            Socket socket = new Socket();
            
            // Установка таймаута
            long timeout = config.getTimeout();
            
            long connectStartTime = System.currentTimeMillis();
            socket.connect(new InetSocketAddress(host, port), (int) timeout);
            connectionTime = System.currentTimeMillis() - connectStartTime;
            
            // Если соединение успешно установлено
            result.setStatus("UP");
            socket.close();
        } catch (IOException e) {
            result.setStatus("DOWN");
            result.setErrorMessage(e.getMessage());
            log.error("Error during TCP check for {}: {}", config.getResourceUrl(), e.getMessage());
        } catch (Exception e) {
            result.setStatus("DOWN");
            result.setErrorMessage(e.getMessage());
            log.error("Unexpected error during TCP check for {}: {}", config.getResourceUrl(), e.getMessage());
        } finally {
            long endTime = System.currentTimeMillis();
            result.setResponseTime(endTime - startTime);
            
            // Установка метрик
            CheckResult.Metrics metrics = new CheckResult.Metrics();
            metrics.setConnectionTime(connectionTime);
            result.setMetrics(metrics);
        }
        
        return result;
    }
    
    @Override
    public boolean supports(String type) {
        return "TCP".equalsIgnoreCase(type);
    }
}