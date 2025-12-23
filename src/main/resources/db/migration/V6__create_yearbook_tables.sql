CREATE TABLE [dbo].[yearbooks](
	[id] [int] IDENTITY(1,1) PRIMARY KEY NOT NULL,
	[head_teacher_user_id] [int] NOT NULL,
	[symbol] [varchar](7) NOT NULL,
	[starting_year] [date] NOT NULL,
	[graduation_year] [date] NOT NULL);

ALTER TABLE [dbo].[yearbooks]  WITH CHECK ADD  CONSTRAINT [FK_yearbooks_teacher] FOREIGN KEY([head_teacher_user_id])
REFERENCES [dbo].[teachers_details] ([user_id]);
ALTER TABLE [dbo].[yearbooks] CHECK CONSTRAINT [FK_yearbooks_teacher];

CREATE TABLE [dbo].[yearbooks_subjects](
	[yearbook_id] [int] NOT NULL,
	[subject_id] [int] NOT NULL);

ALTER TABLE [dbo].[yearbooks_subjects]  WITH CHECK ADD  CONSTRAINT [FK_yearbooks_subjects_subjects1] FOREIGN KEY([subject_id])
REFERENCES [dbo].[subjects] ([id]);
ALTER TABLE [dbo].[yearbooks_subjects] CHECK CONSTRAINT [FK_yearbooks_subjects_subjects1];
ALTER TABLE [dbo].[yearbooks_subjects]  WITH CHECK ADD  CONSTRAINT [FK_yearbooks_subjects_yearbooks1] FOREIGN KEY([yearbook_id])
REFERENCES [dbo].[yearbooks] ([id]);
ALTER TABLE [dbo].[yearbooks_subjects] CHECK CONSTRAINT [FK_yearbooks_subjects_yearbooks1];