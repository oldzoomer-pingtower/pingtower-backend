CREATE TABLE notification_channels (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL CHECK (type IN ('EMAIL', 'TELEGRAM', 'WEBHOOK')),
    enabled BOOLEAN NOT NULL DEFAULT true,
    config JSONB NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

COMMENT ON TABLE notification_channels IS 'Таблица для хранения каналов уведомлений';
COMMENT ON COLUMN notification_channels.type IS 'Тип канала: EMAIL, TELEGRAM, WEBHOOK';
COMMENT ON COLUMN notification_channels.config IS 'Конфигурация канала в формате JSON';