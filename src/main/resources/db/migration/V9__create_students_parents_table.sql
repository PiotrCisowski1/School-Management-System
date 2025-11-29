CREATE TABLE [dbo].[students_parents](
	[parent_id] [int] NOT NULL,
	[student_id] [int] NOT NULL);

ALTER TABLE [dbo].[students_parents]  WITH CHECK ADD  CONSTRAINT [FK_students_parents_users] FOREIGN KEY([parent_id])
REFERENCES [dbo].[parents_details] ([user_id]);
ALTER TABLE [dbo].[students_parents] CHECK CONSTRAINT [FK_students_parents_users];
ALTER TABLE [dbo].[students_parents]  WITH CHECK ADD  CONSTRAINT [FK_students_parents_users1] FOREIGN KEY([student_id])
REFERENCES [dbo].[students_details] ([user_id]);
ALTER TABLE [dbo].[students_parents] CHECK CONSTRAINT [FK_students_parents_users1];