package ru.oldzoomer.pingtower.pinger.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CheckExecutorFactory {
    private final List<CheckExecutor> checkExecutors;
    
    /**
     * Получение исполнителя проверки по типу
     * @param type тип проверки
     * @return исполнитель проверки
     * @throws IllegalArgumentException если исполнитель для указанного типа не найден
     */
    @Cacheable("check-executors")
    public CheckExecutor getExecutor(String type) {
        return checkExecutors.stream()
                .filter(executor -> executor.supports(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No executor found for check type: " + type));
    }
}