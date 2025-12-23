CREATE TABLE [dbo].[parents_details](
	[user_id] [int] PRIMARY KEY NOT NULL);

ALTER TABLE [dbo].[parents_details]  WITH CHECK ADD  CONSTRAINT [FK_parents_details_users] FOREIGN KEY([user_id])
REFERENCES [dbo].[users] ([id]);
ALTER TABLE [dbo].[parents_details] CHECK CONSTRAINT [FK_parents_details_users];