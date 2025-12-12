CREATE TABLE dbo.schedule_occurrences(
    id BIGINT PRIMARY KEY NOT NULL IDENTITY(1,1),
    schedule_id int NOT NULL,
    occurrence_date_time DATETIME NOT NULL,
    occurrence_end_time TIME NOT NULL,
    status VARCHAR(30) NOT NULL
);