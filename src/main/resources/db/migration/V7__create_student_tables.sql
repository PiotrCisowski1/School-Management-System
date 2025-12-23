CREATE TABLE [dbo].[students_details](
	[user_id] [int] PRIMARY KEY NOT NULL,
	[yearbook_id] [int] NOT NULL,
	[date_of_graduation] [date] NULL);

ALTER TABLE [dbo].[students_details]  WITH CHECK ADD  CONSTRAINT [FK_students_details_users1] FOREIGN KEY([user_id])
REFERENCES [dbo].[users] ([id]);
ALTER TABLE [dbo].[students_details] CHECK CONSTRAINT [FK_students_details_users1];
ALTER TABLE [dbo].[students_details]  WITH CHECK ADD  CONSTRAINT [FK_students_details_yearbooks1] FOREIGN KEY([yearbook_id])
REFERENCES [dbo].[yearbooks] ([id]);
ALTER TABLE [dbo].[students_details] CHECK CONSTRAINT [FK_students_details_yearbooks1];