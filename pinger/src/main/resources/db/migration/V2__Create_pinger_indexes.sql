-- Индекс для поиска проверок по типу
CREATE INDEX idx_check_configurations_type ON check_configurations(type);

-- Индекс для поиска проверок по статусу активности
CREATE INDEX idx_check_configurations_enabled ON check_configurations(enabled);

-- Индекс для поиска проверок по интервалу
CREATE INDEX idx_check_configurations_interval ON check_configurations(interval_seconds);

-- Составной индекс для часто используемых запросов
CREATE INDEX idx_check_configurations_type_enabled ON check_configurations(type, enabled);