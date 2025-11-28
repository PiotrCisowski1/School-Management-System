CREATE TABLE [dbo].[subject_type](
	[id] [int] IDENTITY(1,1) PRIMARY KEY NOT NULL,
	[name] [varchar](20) NOT NULL);

CREATE TABLE [dbo].[subjects](
	[id] [int] IDENTITY(1,1) PRIMARY KEY NOT NULL,
	[name] [varchar](20) NOT NULL,
	[code] [varchar](6) NOT NULL,
	[description] [varchar](150) NOT NULL,
	[type_id] [int] NOT NULL);

ALTER TABLE [dbo].[subjects]  WITH CHECK ADD  CONSTRAINT [FK_subjects_subject_type] FOREIGN KEY([type_id])
REFERENCES [dbo].[subject_type] ([id]);
ALTER TABLE [dbo].[subjects] CHECK CONSTRAINT [FK_subjects_subject_type];