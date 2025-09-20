-- Индекс для поиска каналов по типу
CREATE INDEX idx_notification_channels_type ON notification_channels(type);

-- Индекс для поиска каналов по статусу активности
CREATE INDEX idx_notification_channels_enabled ON notification_channels(enabled);

-- Индекс для поиска правил по типу условия
CREATE INDEX idx_notification_rules_condition_type ON notification_rules(condition_type);

-- Индекс для поиска правил по статусу активности
CREATE INDEX idx_notification_rules_enabled ON notification_rules(enabled);

-- Составной индекс для часто используемых запросов к каналам
CREATE INDEX idx_notification_channels_type_enabled ON notification_channels(type, enabled);

-- Составной индекс для часто используемых запросов к правилам
CREATE INDEX idx_notification_rules_condition_type_enabled ON notification_rules(condition_type, enabled);