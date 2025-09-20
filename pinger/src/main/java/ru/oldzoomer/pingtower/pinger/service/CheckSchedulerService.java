package ru.oldzoomer.pingtower.pinger.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.oldzoomer.pingtower.pinger.dto.CheckConfiguration;
import ru.oldzoomer.pingtower.pinger.dto.CheckResult;
import ru.oldzoomer.pingtower.pinger.kafka.CheckResultProducer;
import ru.oldzoomer.pingtower.pinger.kafka.SettingsConsumer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckSchedulerService {
    private final SettingsConsumer settingsConsumer;
    private final CheckExecutorFactory checkExecutorFactory;
    private final CheckResultProducer checkResultProducer;
    
    // Пул потоков для выполнения проверок
    private final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(10);
    
    // Карта запланированных задач
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    
    /**
     * Планирование проверки
     * @param config конфигурация проверки
     */
    public void scheduleCheck(CheckConfiguration config) {
        // Отмена существующей задачи, если она есть
        removeCheck(config.getId());
        
        // Создание задачи для выполнения проверки
        Runnable task = () -> {
            try {
                executeCheck(config);
            } catch (Exception e) {
                log.error("Error executing check for {}: {}", config.getId(), e.getMessage());
            }
        };
        
        // Планирование задачи с указанной частотой
        ScheduledFuture<?> scheduledTask = executorService.scheduleAtFixedRate(
                task,
                0,
                config.getFrequency(),
                TimeUnit.MILLISECONDS
        );
        
        // Сохранение ссылки на задачу для возможности отмены
        scheduledTasks.put(config.getId(), scheduledTask);
        log.info("Scheduled check for {} with frequency {} ms", config.getId(), config.getFrequency());
    }
    
    /**
     * Удаление запланированной проверки
     * @param checkId идентификатор проверки
     */
    public void removeCheck(String checkId) {
        ScheduledFuture<?> scheduledTask = scheduledTasks.get(checkId);
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
            scheduledTasks.remove(checkId);
            log.info("Removed scheduled check for {}", checkId);
        }
    }
    
    /**
     * Выполнение проверки
     * @param config конфигурация проверки
     */
    @Cacheable(value = "last-results", key = "#config.getId()")
    public void executeCheck(CheckConfiguration config) {
        try {
            log.info("Executing check for {} ({})", config.getId(), config.getResourceUrl());
            
            // Получение исполнителя проверки
            CheckExecutor executor = checkExecutorFactory.getExecutor(config.getType());
            
            // Выполнение проверки
            CheckResult result = executor.execute(config);
            
            // Отправка результата в Kafka
            checkResultProducer.sendCheckResult(result);
            
            log.info("Check completed for {}: status={}", config.getId(), result.getStatus());
        } catch (Exception e) {
            log.error("Error executing check for {}: {}", config.getId(), e.getMessage());
        }
    }
    
    /**
     * Планирование всех проверок из конфигурации
     */
    @Scheduled(fixedRate = 60000) // Проверка каждую минуту
    public void scheduleAllChecks() {
        Map<String, CheckConfiguration> configs = settingsConsumer.getCheckConfigurations();
        
        for (CheckConfiguration config : configs.values()) {
            // Если задача еще не запланирована, планируем ее
            if (!scheduledTasks.containsKey(config.getId())) {
                scheduleCheck(config);
            }
        }
    }
}