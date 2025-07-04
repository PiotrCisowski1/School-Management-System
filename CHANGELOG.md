
# 📒 Changelog

All notable changes to this project will be documented in this file.



---

## [1.0.1] - 2025-07-04

### Changed

- Base (centralized) configuration for Mapstruct mappers
- CRUD operations for GradeType (Administrator only)


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

