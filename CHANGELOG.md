
# 📒 Changelog

All notable changes to this project will be documented in this file.



---
## [1.3.3] - 2026-01-04
### New
- Add refresh JWT token endpoint - restricted to authenticated users
- Add 'me' endpoint returning base User data

### Fixed
- Fix DB support for polish characters
- Fix AppConfig lazy init exception on editableBy field

### Changed
- Changed way of creating User - from now Authority will be fetched from DB while creating User
- Removed Parent IDs in patch Student request - flow does not require student side update, Parent cannot be created without children and updates both sides of relation
- Add random UUID to JWT token, to generate more unique tokens

## [1.3.2] - 2026-01-03
### Fixed
- Add custom cors config in SecurityConfiguration restricting API access to hosts from application.properties
- Add missing User's authorities when creating access token

## [1.3.1] - 2025-12-23
RELEASE VERSION

## [1.3.1] - 2025-12-21
### New
- ScheduleOccurrence endpoint to get upcoming lessons for given Yearbook, up to X days (definied in AppConfig as Schedule init time)
- ScheduleOccurrence endpoint to get single occurrence
- Automate ScheduleOccurrence completion - scheduled task checking every 3 hours to change status of ScheduleOccurrence with starting dateTime after particular amount of days (config based value)

### Changed
- fix Schedule deletion - check if any active (ONGOING or COMPLETED) occurrences for Schedule -> if not, set occurrences status to 'CANCELED'
- fix Schedule cancellation - check if there are any active occurrences, if not - set schedule and occurrences to CANCEL status

## [1.3.0] - 2025-12-12
### New
- ScheduleOccurrence - entity related with Schedule as actual Schedule occurrence within given threshold (created automatically based on coming lessons in X days - changed by Administrator in configuration)
- Attendance GET endpoints for Teachers
- Attendance endpoint for summarized Student attendance statistics

### Changed
- Creating Attendance - from now attendance is related to ScheduleOccurrence not Schedule itself

## [1.2.0] - 2025-12-06
### New
- Attendance - automatic initialization of shortly starting scheduled lessons; Endpoint to update Student absence status

## [1.1.1] - 2025-12-02
### New 
- AppConfig - editable (by admin) configuration values used in system

## [1.1.0] - 2025-10-31

### New
- Permission based authorization, securing endpoint based on user role and permission to perform particular REST action 
- Integration tests to check authorization policies for every endpoint and every user type
- Schedule changelog
- Cancel Schedule

### Changed
- Secure every endpoint for particular users
- Changed removal of Schedule and ScheduleVersion to 'soft delete' - in order to keep changelog

## [1.0.2] - 2025-07-18

### New

-CRUD operations on Grade with Teacher permission validation (partial - to be add)


## [1.0.1] - 2025-07-04

### Changed
- Base (centralized) configuration for Mapstruct mappers

### New
- CRUD operations for GradeType (Administrator only)
- CRUD endpoints for GradeScale and GradeValues


## [1.0.0] - 2025-07-03

### Added
- Authentication using JWT (email/password login)
- Role-based access control using Spring Security
- CRUD operations for all user types (`STUDENT`, `TEACHER`, `PARENT`, `ADMINISTRATOR`)
- Management of classrooms and their equipment
- Lesson scheduling with teacher-subject-room-time association
- Tracking of teacher availability for scheduling
- Management of academic years and subject associations
- Event and exception logging to the database
- Unit testing using JUnit 5, Mockito, and Instancio

### In Progress
- Swagger UI for API testing/documentation
- Liquibase database migration setup
- Dockerfile and Docker Compose support
- CI/CD workflow

