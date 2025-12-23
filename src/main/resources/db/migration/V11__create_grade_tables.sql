CREATE TABLE [dbo].[grade_scales](
	[id] [bigint] IDENTITY(1,1) PRIMARY KEY NOT NULL,
	[name] [varchar](100) NOT NULL,
	[description] [varchar](250) NULL,
	[is_active] [bit] NOT NULL,
	[created_at] [datetime] NOT NULL,
	[updated_at] [datetime] NULL,
	[is_hide] [bit] NULL);

CREATE TABLE [dbo].[grade_values](
	[id] [bigint] IDENTITY(1,1) PRIMARY KEY NOT NULL,
	[grade_scale_id] [bigint] NOT NULL,
	[display_value] [varchar](50) NOT NULL,
	[numeric_value] [int] NOT NULL,
	[description] [varchar](200) NULL,
	[is_passing_grade] [bit] NOT NULL,
	[is_hide] [bit] NULL);

ALTER TABLE [dbo].[grade_values]  WITH CHECK ADD  CONSTRAINT [FK_grade_values_grade_scales] FOREIGN KEY([grade_scale_id])
REFERENCES [dbo].[grade_scales] ([id]);
ALTER TABLE [dbo].[grade_values] CHECK CONSTRAINT [FK_grade_values_grade_scales];

CREATE TABLE [dbo].[grade_type](
	[id] [bigint] IDENTITY(1,1) PRIMARY KEY NOT NULL,
	[grade_scope] [varchar](50) NOT NULL UNIQUE,
	[weight] [decimal](3, 2) NOT NULL);

CREATE TABLE [dbo].[grades](
	[id] [bigint] IDENTITY(1,1) PRIMARY KEY NOT NULL,
	[student_id] [int] NOT NULL,
	[teacher_id] [int] NOT NULL,
	[subject_id] [int] NOT NULL,
	[grade_type_id] [bigint] NOT NULL,
	[created_at] [datetime] NULL,
	[comments] [nvarchar](500) NULL,
	[grade_value_id] [bigint] NOT NULL);

ALTER TABLE [dbo].[grades] ADD  CONSTRAINT [DF__grades__date_cre__24E777C3]  DEFAULT (getdate()) FOR [created_at];
ALTER TABLE [dbo].[grades]  WITH CHECK ADD  CONSTRAINT [FK__grades__grade_ty__22FF2F51] FOREIGN KEY([grade_type_id])
REFERENCES [dbo].[grade_type] ([id]);
ALTER TABLE [dbo].[grades] CHECK CONSTRAINT [FK__grades__grade_ty__22FF2F51];
ALTER TABLE [dbo].[grades]  WITH CHECK ADD  CONSTRAINT [FK__grades__student___2022C2A6] FOREIGN KEY([student_id])
REFERENCES [dbo].[students_details] ([user_id]);
ALTER TABLE [dbo].[grades] CHECK CONSTRAINT [FK__grades__student___2022C2A6];
ALTER TABLE [dbo].[grades]  WITH CHECK ADD  CONSTRAINT [FK__grades__subject___220B0B18] FOREIGN KEY([subject_id])
REFERENCES [dbo].[subjects] ([id]);
ALTER TABLE [dbo].[grades] CHECK CONSTRAINT [FK__grades__subject___220B0B18];
ALTER TABLE [dbo].[grades]  WITH CHECK ADD  CONSTRAINT [FK__grades__teacher___2116E6DF] FOREIGN KEY([teacher_id])
REFERENCES [dbo].[teachers_details] ([user_id]);
ALTER TABLE [dbo].[grades] CHECK CONSTRAINT [FK__grades__teacher___2116E6DF];
ALTER TABLE [dbo].[grades]  WITH CHECK ADD  CONSTRAINT [FK_grades_grade_values] FOREIGN KEY([grade_value_id])
REFERENCES [dbo].[grade_values] ([id]);
ALTER TABLE [dbo].[grades] CHECK CONSTRAINT [FK_grades_grade_values];