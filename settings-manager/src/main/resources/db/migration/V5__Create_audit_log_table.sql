CREATE TABLE audit_log (
    id UUID PRIMARY KEY,
    setting_id UUID,
    user_id UUID,
    action VARCHAR(20) NOT NULL,
    old_value TEXT,
    new_value TEXT,
    timestamp TIMESTAMP NOT NULL,
    FOREIGN KEY (setting_id) REFERENCES settings(id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);