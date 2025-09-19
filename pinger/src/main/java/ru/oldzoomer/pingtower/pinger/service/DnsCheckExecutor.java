package ru.oldzoomer.pingtower.pinger.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.oldzoomer.pingtower.pinger.dto.CheckConfiguration;
import ru.oldzoomer.pingtower.pinger.dto.CheckResult;

import java.net.InetAddress;
import java.time.LocalDateTime;

@Slf4j
@Service
public class DnsCheckExecutor implements CheckExecutor {
    
    @Override
    public CheckResult execute(CheckConfiguration config) {
        CheckResult result = new CheckResult();
        result.setCheckId(config.getId());
        result.setResourceUrl(config.getResourceUrl());
        result.setTimestamp(LocalDateTime.now());
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Разбор URL для получения доменного имени
            String host = config.getResourceUrl().replace("dns://", "");
            
            // Выполнение DNS запроса
            InetAddress[] addresses = InetAddress.getAllByName(host);
            
            if (addresses.length > 0) {
                result.setStatus("UP");
            } else {
                result.setStatus("DOWN");
                result.setErrorMessage("No IP addresses found for host: " + host);
            }
        } catch (Exception e) {
            result.setStatus("DOWN");
            result.setErrorMessage(e.getMessage());
            log.error("Error during DNS check for {}: {}", config.getResourceUrl(), e.getMessage());
        } finally {
            long endTime = System.currentTimeMillis();
            result.setResponseTime(endTime - startTime);
        }
        
        return result;
    }
    
    @Override
    public boolean supports(String type) {
        return "DNS".equalsIgnoreCase(type);
    }
}