CREATE TABLE [dbo].[equipments](
	[id] [int] IDENTITY(1,1) PRIMARY KEY NOT NULL,
	[name] [varchar](50) NOT NULL);

CREATE TABLE [dbo].[classrooms](
	[id] [int] IDENTITY(1,1) PRIMARY KEY NOT NULL,
	[name] [varchar](50) NOT NULL,
	[capacity] [int] NOT NULL,
	[notes] [varchar](250) NULL);

CREATE TABLE [dbo].[classroom_equipment](
	[classroom_id] [int] NOT NULL,
	[equipment_id] [int] NOT NULL,
	[quantity] [int] NOT NULL
);

ALTER TABLE [dbo].[classroom_equipment]  WITH CHECK ADD  CONSTRAINT [FK_classroom_equipment_classroom] FOREIGN KEY([classroom_id])
REFERENCES [dbo].[classrooms] ([id]);
ALTER TABLE [dbo].[classroom_equipment] CHECK CONSTRAINT [FK_classroom_equipment_classroom];
ALTER TABLE [dbo].[classroom_equipment]  WITH CHECK ADD  CONSTRAINT [FK_classroom_equipment_equipment] FOREIGN KEY([equipment_id])
REFERENCES [dbo].[equipments] ([id]);
ALTER TABLE [dbo].[classroom_equipment] CHECK CONSTRAINT [FK_classroom_equipment_equipment];