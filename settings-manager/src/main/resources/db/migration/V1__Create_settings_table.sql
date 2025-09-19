CREATE TABLE settings (
    id UUID PRIMARY KEY,
    module VARCHAR(50) NOT NULL,
    key VARCHAR(255) NOT NULL,
    value TEXT,
    description TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version INTEGER NOT NULL
);