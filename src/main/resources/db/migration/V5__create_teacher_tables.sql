CREATE TABLE [dbo].[employees](
	[user_id] [int] PRIMARY KEY NOT NULL,
	[employment_start_date] [date] NOT NULL,
	[employment_end_date] [date] NULL);

ALTER TABLE [dbo].[employees]  WITH CHECK ADD  CONSTRAINT [FK_employees_users] FOREIGN KEY([user_id])
REFERENCES [dbo].[users] ([id]);
ALTER TABLE [dbo].[employees] CHECK CONSTRAINT [FK_employees_users];

CREATE TABLE [dbo].[teachers_details](
	[user_id] [int] PRIMARY KEY NOT NULL);

ALTER TABLE [dbo].[teachers_details]  WITH CHECK ADD  CONSTRAINT [FK_teachers_details_users] FOREIGN KEY([user_id])
REFERENCES [dbo].[users] ([id]);
ALTER TABLE [dbo].[teachers_details] CHECK CONSTRAINT [FK_teachers_details_users];

CREATE TABLE [dbo].[teachers_availability](
	[id] [int] IDENTITY(1,1) PRIMARY KEY NOT NULL,
	[teacher_id] [int] NOT NULL,
	[day_of_week] [int] NOT NULL,
	[start_time] [time](7) NOT NULL,
	[end_time] [time](7) NOT NULL,
	[is_available] [bit] NOT NULL,
	[notes] [varchar](200) NULL);

ALTER TABLE [dbo].[teachers_availability]  WITH CHECK ADD  CONSTRAINT [FK_teachers_availability_teachers_availability] FOREIGN KEY([teacher_id])
REFERENCES [dbo].[teachers_details] ([user_id]);
ALTER TABLE [dbo].[teachers_availability] CHECK CONSTRAINT [FK_teachers_availability_teachers_availability];

CREATE TABLE [dbo].[teachers_subjects](
	[teacher_details_id] [int] NOT NULL,
	[subject_id] [int] NOT NULL);

ALTER TABLE [dbo].[teachers_subjects]  WITH CHECK ADD  CONSTRAINT [FK_teachers_subjects_subjects] FOREIGN KEY([subject_id])
REFERENCES [dbo].[subjects] ([id]);
ALTER TABLE [dbo].[teachers_subjects] CHECK CONSTRAINT [FK_teachers_subjects_subjects];
ALTER TABLE [dbo].[teachers_subjects]  WITH CHECK ADD  CONSTRAINT [FK_teachers_subjects_teachers_details] FOREIGN KEY([teacher_details_id])
REFERENCES [dbo].[teachers_details] ([user_id]);
ALTER TABLE [dbo].[teachers_subjects] CHECK CONSTRAINT [FK_teachers_subjects_teachers_details];