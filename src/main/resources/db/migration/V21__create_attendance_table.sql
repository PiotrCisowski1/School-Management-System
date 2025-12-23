CREATE TABLE dbo.attendances(
	id bigint IDENTITY(1,1) PRIMARY KEY NOT NULL,
	student_id INT NOT NULL,
	attendance_status VARCHAR(30) NOT NULL,
	created_at DATETIME NOT NULL,
	last_modified_at DATETIME,
	last_modified_by_user_id INT,
	occurrence_id BIGINT NOT NULL);

ALTER TABLE attendances WITH CHECK ADD CONSTRAINT FK_attendance_student FOREIGN KEY(student_id)
REFERENCES students_details (user_id);
ALTER TABLE attendances WITH CHECK ADD CONSTRAINT FK_attendance_last_modification_user FOREIGN KEY(last_modified_by_user_id)
REFERENCES users (id);
ALTER TABLE attendances WITH CHECK ADD CONSTRAINT FK_attendance_occurrence FOREIGN KEY(occurrence_id)
REFERENCES schedule_occurrences (id);