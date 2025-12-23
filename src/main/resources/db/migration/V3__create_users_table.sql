CREATE TABLE [dbo].[address](
	[id] [int] IDENTITY(1,1) PRIMARY KEY NOT NULL,
	[city] [varchar](50) NOT NULL,
	[street] [varchar](50) NULL,
	[building_number] [varchar](7) NOT NULL,
	[voivodeship] [varchar](80) NOT NULL,
	[zip_code] [varchar](6) NOT NULL);

CREATE TABLE [dbo].[users](
	[id] [int] IDENTITY(1,1) PRIMARY KEY NOT NULL,
	[email] [varchar](150) UNIQUE NOT NULL,
	[password] [varchar](100) NOT NULL,
	[enabled] [bit] NOT NULL,
	[phone_number] [varchar](15) NULL,
	[first_name] [varchar](25) NOT NULL,
	[last_name] [varchar](50) NOT NULL,
	[birth_date] [date] NOT NULL,
	[gender] [varchar](10) NOT NULL,
	[address_id] [int] NOT NULL,
	[date_of_creation] [datetime] NOT NULL,
	[date_of_update] [datetime] NULL,
	[user_type] [varchar](15) NULL);

ALTER TABLE [dbo].[users]  WITH CHECK ADD CONSTRAINT [FK_users_address] FOREIGN KEY([address_id])
REFERENCES [dbo].[address] ([id]);

CREATE TABLE [dbo].[users_authorities](
	[user_id] [int] NOT NULL,
	[authority_id] [int] NOT NULL
);

ALTER TABLE [dbo].[users_authorities]  WITH CHECK ADD  CONSTRAINT [FK_users_authorities_authorities] FOREIGN KEY([authority_id])
REFERENCES [dbo].[authorities] ([id]);
ALTER TABLE [dbo].[users_authorities] CHECK CONSTRAINT [FK_users_authorities_authorities];

ALTER TABLE [dbo].[users_authorities]  WITH CHECK ADD  CONSTRAINT [FK_users_authorities_users] FOREIGN KEY([user_id])
REFERENCES [dbo].[users] ([id]);
ALTER TABLE [dbo].[users_authorities] CHECK CONSTRAINT [FK_users_authorities_users];


