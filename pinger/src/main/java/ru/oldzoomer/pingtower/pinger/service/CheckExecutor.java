package ru.oldzoomer.pingtower.pinger.service;

import ru.oldzoomer.pingtower.pinger.dto.CheckConfiguration;
import ru.oldzoomer.pingtower.pinger.dto.CheckResult;

/**
 * Интерфейс для выполнения проверок доступности ресурсов
 */
public interface CheckExecutor {
    /**
     * Выполняет проверку доступности ресурса
     * @param config конфигурация проверки
     * @return результат проверки
     */
    CheckResult execute(CheckConfiguration config);
    
    /**
     * Проверяет, поддерживает ли данный исполнитель указанный тип проверки
     * @param type тип проверки
     * @return true, если тип поддерживается, иначе false
     */
    boolean supports(String type);
}