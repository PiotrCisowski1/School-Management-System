CREATE TABLE [dbo].[authorities](
	[id] [int] IDENTITY(1,1) PRIMARY KEY NOT NULL,
	[authority] [varchar](30) NOT NULL);

INSERT INTO authorities(authority) VALUES ('SYS_ADMIN');
INSERT INTO authorities(authority) VALUES ('ADMINISTRATOR');
INSERT INTO authorities(authority) VALUES ('STUDENT');
INSERT INTO authorities(authority) VALUES ('PARENT');
INSERT INTO authorities(authority) VALUES ('TEACHER');