CREATE TABLE [dbo].[logging_event](
	[timestmp] [decimal](20, 0) NOT NULL,
	[formatted_message] [varchar](4000) NOT NULL,
	[logger_name] [varchar](254) NOT NULL,
	[level_string] [varchar](254) NOT NULL,
	[thread_name] [varchar](254) NULL,
	[reference_flag] [smallint] NULL,
	[arg0] [varchar](254) NULL,
	[arg1] [varchar](254) NULL,
	[arg2] [varchar](254) NULL,
	[arg3] [varchar](254) NULL,
	[caller_filename] [varchar](254) NOT NULL,
	[caller_class] [varchar](254) NOT NULL,
	[caller_method] [varchar](254) NOT NULL,
	[caller_line] [char](4) NOT NULL,
	[event_id] [decimal](38, 0) PRIMARY KEY IDENTITY(1,1) NOT NULL);

CREATE TABLE [dbo].[logging_event_exception](
	[event_id] [decimal](38, 0) NOT NULL,
	[i] [smallint] NOT NULL,
	[trace_line] [varchar](254) NOT NULL,
	PRIMARY KEY (event_id, i));

ALTER TABLE [dbo].[logging_event_exception]  WITH CHECK ADD FOREIGN KEY([event_id])
REFERENCES [dbo].[logging_event] ([event_id]);

CREATE TABLE [dbo].[logging_event_property](
	[event_id] [decimal](38, 0) NOT NULL,
	[mapped_key] [varchar](254) NOT NULL,
	[mapped_value] [varchar](1024) NULL,
	PRIMARY KEY (event_id, mapped_key));

ALTER TABLE [dbo].[logging_event_property]  WITH CHECK ADD FOREIGN KEY([event_id])
REFERENCES [dbo].[logging_event] ([event_id]);