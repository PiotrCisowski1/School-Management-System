CREATE TABLE [dbo].[schedule_version](
	[id] [int] IDENTITY(1,1) PRIMARY KEY NOT NULL,
	[create_date] [datetime] NOT NULL,
	[is_active] [bit] NOT NULL,
	[yearbook_id] [int] NOT NULL,
	[name] [varchar](20) NOT NULL,
	[status] [varchar](50) NOT NULL);

ALTER TABLE [dbo].[schedule_version] ADD  CONSTRAINT [DF_schedule_version_is_active]  DEFAULT ((0)) FOR [is_active];
ALTER TABLE [dbo].[schedule_version]  WITH CHECK ADD  CONSTRAINT [FK_schedule_version_yearbooks] FOREIGN KEY([yearbook_id])
REFERENCES [dbo].[yearbooks] ([id]);
ALTER TABLE [dbo].[schedule_version] CHECK CONSTRAINT [FK_schedule_version_yearbooks];

CREATE TABLE [dbo].[schedules](
	[id] [int] IDENTITY(1,1) PRIMARY KEY  NOT NULL,
	[schedule_version_id] [int] NOT NULL,
	[subject_id] [int] NOT NULL,
	[teacher_id] [int] NOT NULL,
	[classroom_id] [int] NOT NULL,
	[day_of_week] [int] NOT NULL,
	[start_time] [time](7) NOT NULL,
	[end_time] [time](7) NOT NULL,
	[recurrence_type] [varchar](15) NOT NULL,
	[status] [varchar](50) NOT NULL,
	[effective_date] [date] NOT NULL,
	[expiration_date] [date] NULL);

ALTER TABLE [dbo].[schedules]  WITH CHECK ADD  CONSTRAINT [FK_schedule_lesson_classroom] FOREIGN KEY([classroom_id])
REFERENCES [dbo].[classrooms] ([id]);
ALTER TABLE [dbo].[schedules] CHECK CONSTRAINT [FK_schedule_lesson_classroom];
ALTER TABLE [dbo].[schedules]  WITH CHECK ADD  CONSTRAINT [FK_schedule_lesson_schedule_version] FOREIGN KEY([schedule_version_id])
REFERENCES [dbo].[schedule_version] ([id]);
ALTER TABLE [dbo].[schedules] CHECK CONSTRAINT [FK_schedule_lesson_schedule_version];
ALTER TABLE [dbo].[schedules]  WITH CHECK ADD  CONSTRAINT [FK_schedule_lesson_subjects] FOREIGN KEY([subject_id])
REFERENCES [dbo].[subjects] ([id]);
ALTER TABLE [dbo].[schedules] CHECK CONSTRAINT [FK_schedule_lesson_subjects];
ALTER TABLE [dbo].[schedules]  WITH CHECK ADD  CONSTRAINT [FK_schedule_lesson_teachers_details] FOREIGN KEY([teacher_id])
REFERENCES [dbo].[teachers_details] ([user_id]);
ALTER TABLE [dbo].[schedules] CHECK CONSTRAINT [FK_schedule_lesson_teachers_details];