USE [master]
GO

CREATE DATABASE [VeloSA]
 CONTAINMENT = NONE
GO

/*    ==Scripting Parameters==

    Source Server Version : SQL Server 2017 (14.0.1000)
    Source Database Engine Edition : Microsoft SQL Server Enterprise Edition
    Source Database Engine Type : Standalone SQL Server

    Target Server Version : SQL Server 2017
    Target Database Engine Edition : Microsoft SQL Server Standard Edition
    Target Database Engine Type : Standalone SQL Server
*/
USE [VeloSA]
GO
ALTER TABLE [dbo].[Vehicles] DROP CONSTRAINT [FK_Vehicles_Locks]
GO
ALTER TABLE [dbo].[Vehicles] DROP CONSTRAINT [FK_Bikes_BikeLots]
GO
ALTER TABLE [dbo].[Subscriptions] DROP CONSTRAINT [FK_Subscriptions_Users]
GO
ALTER TABLE [dbo].[Subscriptions] DROP CONSTRAINT [FK_Subscriptions_SubscriptionTypes]
GO
ALTER TABLE [dbo].[Rides] DROP CONSTRAINT [FK_Rides_Vehicles]
GO
ALTER TABLE [dbo].[Rides] DROP CONSTRAINT [FK_Rides_Subscriptions]
GO
ALTER TABLE [dbo].[Rides] DROP CONSTRAINT [FK_Rides_Locks_Start]
GO
ALTER TABLE [dbo].[Rides] DROP CONSTRAINT [FK_Rides_Locks_End]
GO
ALTER TABLE [dbo].[Rides] DROP CONSTRAINT [FK_Rides_Employees]
GO
ALTER TABLE [dbo].[Rides] DROP CONSTRAINT [FK_Rides_BikeMaintenanceTypes]
GO
ALTER TABLE [dbo].[Locks] DROP CONSTRAINT [FK_Locks_Vehicles]
GO
ALTER TABLE [dbo].[Locks] DROP CONSTRAINT [FK_Locks_Stations]
GO
ALTER TABLE [dbo].[Locks] DROP CONSTRAINT [FK_Locks_Locks]
GO
ALTER TABLE [dbo].[Bikelots] DROP CONSTRAINT [FK_Bikelots_BikeTypes]
GO
ALTER TABLE [dbo].[Bikelots] DROP CONSTRAINT [FK_Bikelots_Bikelots]
GO
/****** Object:  Table [dbo].[Vehicles]    Script Date: 13/09/2019 9:44:41 ******/
DROP TABLE [dbo].[Vehicles]
GO
/****** Object:  Table [dbo].[Users]    Script Date: 13/09/2019 9:44:41 ******/
DROP TABLE [dbo].[Users]
GO
/****** Object:  Table [dbo].[SubscriptionTypes]    Script Date: 13/09/2019 9:44:41 ******/
DROP TABLE [dbo].[SubscriptionTypes]
GO
/****** Object:  Table [dbo].[Subscriptions]    Script Date: 13/09/2019 9:44:41 ******/
DROP TABLE [dbo].[Subscriptions]
GO
/****** Object:  Table [dbo].[Stations]    Script Date: 13/09/2019 9:44:41 ******/
DROP TABLE [dbo].[Stations]
GO
/****** Object:  Table [dbo].[Rides]    Script Date: 13/09/2019 9:44:41 ******/
DROP TABLE [dbo].[Rides]
GO
/****** Object:  Table [dbo].[Locks]    Script Date: 13/09/2019 9:44:41 ******/
DROP TABLE [dbo].[Locks]
GO
/****** Object:  Table [dbo].[Employees]    Script Date: 13/09/2019 9:44:41 ******/
DROP TABLE [dbo].[Employees]
GO
/****** Object:  Table [dbo].[BikeTypes]    Script Date: 13/09/2019 9:44:41 ******/
DROP TABLE [dbo].[BikeTypes]
GO
/****** Object:  Table [dbo].[BikeMaintenanceTypes]    Script Date: 13/09/2019 9:44:41 ******/
DROP TABLE [dbo].[BikeMaintenanceTypes]
GO
/****** Object:  Table [dbo].[Bikelots]    Script Date: 13/09/2019 9:44:41 ******/
DROP TABLE [dbo].[Bikelots]
GO
/****** Object:  Table [dbo].[Bikelots]    Script Date: 13/09/2019 9:44:41 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Bikelots](
	[BikeLotId] [smallint] IDENTITY(1,1) NOT NULL,
	[DeliveryDate] [date] NULL,
	[BikeTypeId] [tinyint] NULL,
 CONSTRAINT [PK_BikeLots] PRIMARY KEY CLUSTERED
(
	[BikeLotId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[BikeMaintenanceTypes]    Script Date: 13/09/2019 9:44:41 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[BikeMaintenanceTypes](
	[BikeMaintenanceTypeId] [tinyint] NOT NULL,
	[Description] [nvarchar](50) NULL,
PRIMARY KEY CLUSTERED
(
	[BikeMaintenanceTypeId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[BikeTypes]    Script Date: 13/09/2019 9:44:41 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[BikeTypes](
	[BikeTypeId] [tinyint] NOT NULL,
	[BikeTypeDescription] [nvarchar](200) NULL,
 CONSTRAINT [PK_BikeTypes] PRIMARY KEY CLUSTERED
(
	[BikeTypeId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Employees]    Script Date: 13/09/2019 9:44:41 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Employees](
	[EmployeeId] [smallint] IDENTITY(1,1) NOT NULL,
	[Name] [nvarchar](100) NOT NULL,
	[Email] [nvarchar](100) NULL,
	[Street] [nvarchar](100) NULL,
	[Number] [nvarchar](10) NULL,
	[Zipcode] [nvarchar](10) NULL,
	[City] [nvarchar](100) NULL,
	[ExperienceLevel] [tinyint] NOT NULL,
	[HourlyRate] [tinyint] NOT NULL,
 CONSTRAINT [PK_Employees] PRIMARY KEY CLUSTERED
(
	[EmployeeId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Locks]    Script Date: 13/09/2019 9:44:41 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Locks](
	[LockId] [smallint] NOT NULL,
	[StationLockNr] [tinyint] NOT NULL,
	[StationId] [smallint] NOT NULL,
	[VehicleId] [smallint] NULL,
 CONSTRAINT [PK_Locks] PRIMARY KEY CLUSTERED
(
	[LockId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Rides]    Script Date: 13/09/2019 9:44:41 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Rides](
	[RideId] [bigint] IDENTITY(1,1) NOT NULL,
	[StartPoint] [geometry] NULL,
	[EndPoint] [geometry] NULL,
	[StartTime] [datetime] NULL,
	[EndTime] [datetime] NULL,
	[VehicleId] [smallint] NOT NULL,
	[SubscriptionId] [int] NULL,
	[EmployeeId] [smallint] NULL,
	[Startlockid] [smallint] NULL,
	[EndLockId] [smallint] NULL,
	[MaintenanceTypeId] [tinyint] NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Stations]    Script Date: 13/09/2019 9:44:41 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Stations](
	[StationId] [smallint] IDENTITY(1,1) NOT NULL,
	[ObjectId] [nvarchar](20) NOT NULL,
	[StationNr] [nvarchar](20) NOT NULL,
	[Type] [nvarchar](20) NOT NULL,
	[Street] [nvarchar](100) NOT NULL,
	[Number] [nvarchar](10) NULL,
	[ZipCode] [nvarchar](10) NOT NULL,
	[District] [nvarchar](100) NOT NULL,
	[GPSCoord] [geometry] NOT NULL,
	[AdditionalInfo] [nvarchar](100) NULL,
	[LabelId] [tinyint] NULL,
	[CityId] [tinyint] NULL,
 CONSTRAINT [PK_Stations] PRIMARY KEY CLUSTERED
(
	[StationId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Subscriptions]    Script Date: 13/09/2019 9:44:41 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Subscriptions](
	[SubscriptionId] [int] IDENTITY(1,1) NOT NULL,
	[ValidFrom] [date] NOT NULL,
	[SubscriptionTypeId] [tinyint] NOT NULL,
	[UserId] [int] NOT NULL,
 CONSTRAINT [PK_Subscriptions] PRIMARY KEY CLUSTERED
(
	[SubscriptionId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[SubscriptionTypes]    Script Date: 13/09/2019 9:44:41 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[SubscriptionTypes](
	[SubscriptionTypeId] [tinyint] IDENTITY(1,1) NOT NULL,
	[Description] [nvarchar](50) NULL,
 CONSTRAINT [PK_SubscriptionTypes] PRIMARY KEY CLUSTERED
(
	[SubscriptionTypeId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Users]    Script Date: 13/09/2019 9:44:41 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Users](
	[UserId] [int] IDENTITY(1,1) NOT NULL,
	[Name] [nvarchar](100) NULL,
	[Email] [nvarchar](100) NOT NULL,
	[Street] [nvarchar](100) NULL,
	[Number] [nvarchar](10) NULL,
	[Zipcode] [nvarchar](10) NULL,
	[City] [nvarchar](100) NULL,
	[CountryCode] [nvarchar](3) NULL,
 CONSTRAINT [PK_Users] PRIMARY KEY CLUSTERED
(
	[UserId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Vehicles]    Script Date: 13/09/2019 9:44:41 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Vehicles](
	[VehicleId] [smallint] IDENTITY(1,1) NOT NULL,
	[SerialNumber] [nvarchar](20) NOT NULL,
	[BikeLotId] [smallint] NOT NULL,
	[LastMaintenanceOn] [datetime] NULL,
	[LockId] [smallint] NULL,
	[Point] [geometry] NULL,
 CONSTRAINT [PK_Bikes] PRIMARY KEY CLUSTERED
(
	[VehicleId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO