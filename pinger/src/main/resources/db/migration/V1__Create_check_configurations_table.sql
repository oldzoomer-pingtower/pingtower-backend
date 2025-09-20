CREATE TABLE check_configurations (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL CHECK (type IN ('HTTP', 'TCP', 'DNS')),
    target VARCHAR(255) NOT NULL,
    interval_seconds INTEGER NOT NULL,
    timeout_millis INTEGER NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    notification_rule_ids JSONB,
    additional_config JSONB,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

COMMENT ON TABLE check_configurations IS 'Таблица для хранения конфигураций проверок доступности';
COMMENT ON COLUMN check_configurations.type IS 'Тип проверки: HTTP, TCP, DNS';
COMMENT ON COLUMN check_configurations.target IS 'Целевой адрес для проверки';
COMMENT ON COLUMN check_configurations.interval_seconds IS 'Интервал проверки в секундах';
COMMENT ON COLUMN check_configurations.timeout_millis IS 'Таймаут проверки в миллисекундах';
COMMENT ON COLUMN check_configurations.notification_rule_ids IS 'Идентификаторы правил уведомлений в формате JSON массива';
COMMENT ON COLUMN check_configurations.additional_config IS 'Дополнительная конфигурация в формате JSON';