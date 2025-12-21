CREATE TABLE app_config(
    id BIGINT PRIMARY KEY NOT NULL IDENTITY(1,1),
    [key] VARCHAR(100) NOT NULL UNIQUE,
    [value] VARCHAR(150) NOT NULL,
    value_type VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    is_editable BIT NOT NULL,
    min_value VARCHAR(30),
    max_value VARCHAR(30),
    created_at DATETIME NOT NULL,
    modified_by INTEGER,
    modified_at DATETIME,
);

ALTER TABLE app_config WITH CHECK ADD CONSTRAINT FK_app_config_users FOREIGN KEY(modified_by)
REFERENCES users (id);

