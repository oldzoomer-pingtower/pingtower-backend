CREATE INDEX idx_settings_module ON settings(module);
CREATE INDEX idx_settings_module_key ON settings(module, key);
CREATE INDEX idx_audit_log_setting_id ON audit_log(setting_id);
CREATE INDEX idx_audit_log_user_id ON audit_log(user_id);
CREATE INDEX idx_audit_log_timestamp ON audit_log(timestamp);