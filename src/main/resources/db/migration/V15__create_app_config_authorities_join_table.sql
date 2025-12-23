CREATE TABLE app_config_authorities(
    app_config_key VARCHAR(100) NOT NULL,
    authority_id INTEGER NOT NULL
);

ALTER TABLE app_config_authorities WITH CHECK ADD CONSTRAINT FK_app_config_key FOREIGN KEY(app_config_key)
REFERENCES app_config ([key]);
ALTER TABLE app_config_authorities WITH CHECK ADD CONSTRAINT FK_authorities_id FOREIGN KEY(authority_id)
REFERENCES authorities (id);