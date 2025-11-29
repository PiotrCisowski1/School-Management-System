CREATE TABLE [dbo].[schedule_change_log](
	[id] [bigint] IDENTITY(1,1) PRIMARY KEY NOT NULL,
	[schedule_id] [int] NOT NULL,
	[change_type] [varchar](50) NOT NULL,
	[field_name] [varchar](50) NULL,
	[old_value] [varchar](50) NULL,
	[new_value] [varchar](50) NOT NULL,
	[changed_by_user_id] [bigint] NOT NULL,
	[changed_at] [date] NOT NULL,
	[reason] [varchar](200) NULL,
	[automatic_change] [bit] NOT NULL);

ALTER TABLE [dbo].[schedule_change_log]  WITH CHECK ADD  CONSTRAINT [FK_schedule_change_log_schedules] FOREIGN KEY([schedule_id])
REFERENCES [dbo].[schedules] ([id]);
ALTER TABLE [dbo].[schedule_change_log] CHECK CONSTRAINT [FK_schedule_change_log_schedules];

CREATE TABLE [dbo].[schedule_changelog_affected_users](
	[user_id] [int] NOT NULL,
	[schedule_changelog_id] [bigint] NOT NULL
);

ALTER TABLE [dbo].[schedule_changelog_affected_users]  WITH CHECK ADD  CONSTRAINT [FK_schedule_changelog_affected_users_schedule_change_log] FOREIGN KEY([schedule_changelog_id])
REFERENCES [dbo].[schedule_change_log] ([id]);
ALTER TABLE [dbo].[schedule_changelog_affected_users] CHECK CONSTRAINT [FK_schedule_changelog_affected_users_schedule_change_log];
ALTER TABLE [dbo].[schedule_changelog_affected_users]  WITH CHECK ADD  CONSTRAINT [FK_schedule_changelog_affected_users_users] FOREIGN KEY([user_id])
REFERENCES [dbo].[users] ([id]);
ALTER TABLE [dbo].[schedule_changelog_affected_users] CHECK CONSTRAINT [FK_schedule_changelog_affected_users_users];