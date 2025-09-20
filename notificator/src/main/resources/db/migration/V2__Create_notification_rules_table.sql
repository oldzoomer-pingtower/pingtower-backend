CREATE TABLE notification_rules (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    condition_type VARCHAR(50) NOT NULL CHECK (condition_type IN ('STATUS_CHANGE', 'THRESHOLD', 'SCHEDULED')),
    condition_config JSONB NOT NULL,
    channel_ids JSONB NOT NULL,
    escalation_config JSONB,
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

COMMENT ON TABLE notification_rules IS 'Таблица для хранения правил уведомлений';
COMMENT ON COLUMN notification_rules.condition_type IS 'Тип условия: STATUS_CHANGE, THRESHOLD, SCHEDULED';
COMMENT ON COLUMN notification_rules.condition_config IS 'Конфигурация условия в формате JSON';
COMMENT ON COLUMN notification_rules.channel_ids IS 'Идентификаторы каналов уведомлений в формате JSON массива';
COMMENT ON COLUMN notification_rules.escalation_config IS 'Конфигурация эскалации в формате JSON';