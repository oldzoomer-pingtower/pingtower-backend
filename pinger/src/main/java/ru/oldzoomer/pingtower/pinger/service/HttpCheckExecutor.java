package ru.oldzoomer.pingtower.pinger.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.oldzoomer.pingtower.pinger.dto.CheckConfiguration;
import ru.oldzoomer.pingtower.pinger.dto.CheckResult;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class HttpCheckExecutor implements CheckExecutor {
    
    @Override
    public CheckResult execute(CheckConfiguration config) {
        CheckResult result = new CheckResult();
        result.setCheckId(config.getId());
        result.setResourceUrl(config.getResourceUrl());
        result.setTimestamp(LocalDateTime.now());
        
        long startTime = System.currentTimeMillis();
        long connectionTime = 0;
        long timeToFirstByte = 0;
        
        try {
            URL url = createURL(config.getResourceUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            // Установка таймаутов
            connection.setConnectTimeout(config.getTimeout());
            connection.setReadTimeout(config.getTimeout());
            
            connectionTime = System.currentTimeMillis() - startTime;
            
            // Подключение к ресурсу и получение первого байта
            connection.connect();
            
            timeToFirstByte = System.currentTimeMillis() - startTime;
            
            // Чтение первого байта данных
            try (InputStream inputStream = connection.getInputStream()) {
                inputStream.read();
            } catch (IOException e) {
                // Игнорируем ошибки чтения, если они не критичны
                log.debug("Error reading first byte from {}: {}", config.getResourceUrl(), e.getMessage());
            }
            
            // Получение кода ответа
            int responseCode = connection.getResponseCode();
            result.setHttpStatusCode(responseCode);
            
            // Проверка кода ответа
            if (responseCode >= 200 && responseCode < 300) {
                result.setStatus("UP");
                // Проверка на соответствие ожидаемому коду (если указан)
                if (config.getExpectedStatusCode() != null && responseCode != config.getExpectedStatusCode()) {
                    result.setStatus("DOWN");
                    result.setErrorMessage("Expected status code " + config.getExpectedStatusCode() + ", but got " + responseCode);
                }
            } else {
                result.setStatus("DOWN");
                result.setErrorMessage("HTTP error code: " + responseCode);
            }
            
            // Если это HTTPS, проверяем SSL сертификат
            if (connection instanceof HttpsURLConnection) {
                checkSslCertificate((HttpsURLConnection) connection, result, config.isValidateSsl());
            }
            
            connection.disconnect();
        } catch (Exception e) {
            result.setStatus("DOWN");
            result.setErrorMessage(e.getMessage());
            log.error("Error during HTTP check for {}: {}", config.getResourceUrl(), e.getMessage());
        } finally {
            long endTime = System.currentTimeMillis();
            result.setResponseTime(endTime - startTime);
            
            // Установка метрик (если еще не установлены)
            if (result.getMetrics() == null) {
                CheckResult.Metrics metrics = new CheckResult.Metrics();
                metrics.setConnectionTime(connectionTime);
                metrics.setTimeToFirstByte(timeToFirstByte);
                result.setMetrics(metrics);
            }
        }
        
        return result;
    }
    
    @Override
    public boolean supports(String type) {
        return "HTTP".equalsIgnoreCase(type) || "HTTPS".equalsIgnoreCase(type);
    }

    /**
     * Создание URL из строки (метод для тестирования)
     * @param urlString строка URL
     * @return объект URL
     * @throws Exception если URL некорректен
     */
    protected URL createURL(String urlString) throws Exception {
        return new URI(urlString).toURL();
    }
    
    /**
     * Проверка SSL сертификата
     * @param connection HTTPS соединение
     * @param result результат проверки
     * @param validateSsl флаг необходимости проверки SSL
     */
    private void checkSslCertificate(HttpsURLConnection connection, CheckResult result, boolean validateSsl) {
        try {
            // Инициализация метрик, если они еще не созданы
            if (result.getMetrics() == null) {
                CheckResult.Metrics metrics = new CheckResult.Metrics();
                result.setMetrics(metrics);
            }
            
            // Получение сертификатов
            Certificate[] certificates = connection.getServerCertificates();
            
            if (certificates != null && certificates.length > 0) {
                // Проверка первого сертификата (сертификата сервера)
                Certificate certificate = certificates[0];
                
                if (certificate instanceof X509Certificate x509Cert) {

                    // Установка флага валидности SSL
                    result.getMetrics().setSslValid(true);
                    
                    // Установка даты истечения сертификата
                    result.getMetrics().setSslExpirationDate(x509Cert.getNotAfter().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
                    
                    log.debug("SSL certificate is valid for {}: expires on {}",
                            connection.getURL().getHost(),
                            x509Cert.getNotAfter());
                }
            }
        } catch (Exception e) {
            // Другие ошибки SSL
            if (result.getMetrics() != null) {
                result.getMetrics().setSslValid(false);
            }
            result.setErrorMessage("SSL certificate error: " + e.getMessage());
            log.error("SSL certificate error for {}: {}", connection.getURL().getHost(), e.getMessage());
        }
    }
}