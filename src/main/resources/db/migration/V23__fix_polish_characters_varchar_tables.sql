
ALTER TABLE [dbo].[address] ALTER COLUMN [city] [nvarchar](50) NOT NULL;
ALTER TABLE [dbo].[address] ALTER COLUMN [street] [nvarchar](50) NULL;
ALTER TABLE [dbo].[address] ALTER COLUMN [building_number] [nvarchar](7) NOT NULL;
ALTER TABLE [dbo].[address] ALTER COLUMN [voivodeship] [nvarchar](80) NOT NULL;
ALTER TABLE [dbo].[address] ALTER COLUMN [zip_code] [nvarchar](6) NOT NULL;

ALTER TABLE [dbo].[users] ALTER COLUMN [phone_number] [nvarchar](15) NULL;
ALTER TABLE [dbo].[users] ALTER COLUMN [first_name] [nvarchar](25) NOT NULL;
ALTER TABLE [dbo].[users] ALTER COLUMN [last_name] [nvarchar](50) NOT NULL;
ALTER TABLE [dbo].[users] ALTER COLUMN [gender] [nvarchar](10) NOT NULL;
ALTER TABLE [dbo].[users] ALTER COLUMN [user_type] [nvarchar](15) NULL;

ALTER TABLE [dbo].[subject_type] ALTER COLUMN [name] [nvarchar](20) NOT NULL;
ALTER TABLE [dbo].[subjects] ALTER COLUMN [name] [nvarchar](20) NOT NULL;
ALTER TABLE [dbo].[subjects] ALTER COLUMN [code] [nvarchar](6) NOT NULL;
ALTER TABLE [dbo].[subjects] ALTER COLUMN [description] [nvarchar](150) NOT NULL;

ALTER TABLE [dbo].[yearbooks] ALTER COLUMN [symbol] [nvarchar](7) NOT NULL;