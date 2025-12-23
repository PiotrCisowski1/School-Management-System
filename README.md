# 🏫 School Management System

A modular and extensible backend system for managing a school's daily operations – built with Java 17 and Spring Boot.  
This project serves both as an educational journey and a professional showcase of clean code, domain modeling and security in Java applications.

> **Status:** Stable version `v1.3.1`  
> Project under active development. 

---

## 📚 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Authentication & Authorization](#authentication-and-authorization)
- [Testing](#testing)
- [Changelog](#changelog)
- [License](#license)
- [Author](#author)

---

## Features 

- 🔐 **User Management**
  - Roles: `STUDENT`, `TEACHER`, `PARENT`, `ADMINISTRATOR`
  - CRUD operations for users and permissions
- 🏫 **Classroom Management**
  - Manage classrooms and inventory/equipment
- 📆 **Lesson Scheduling**
  - Assign subjects and teachers to time slots and classrooms
  - Validate teacher availability
  - Automatic schedule occurrences creation and completion
  - Attendance marking
- 📚 **Subject & Year Structure**
  - Assign subjects to academic years and groups
- ⚙️ **App Configuration**
  - Administrator-editable values, changing system behavior (such as a time threshold for attendance marking)
- 📈 **Event Logging**
  - System events and exceptions are persisted to the database
- 🔐 **Secure Access**
  - Email/password login
  - JWT-based token authentication
  - Role-based endpoint security via Spring Security

---

## Tech Stack 

| Layer         | Technology                                                     |
|--------------|----------------------------------------------------------------|
| Language      | Java 17                                                        |
| Framework     | Spring Boot, Spring Security, Spring Data JPA                  |
| Build Tool    | Maven                                                          |
| Database      | Microsoft SQL Server as main, PostgreSQL for integration tests |
| Auth          | JWT Token-based Authentication                                 |
| Utilities     | Lombok, MapStruct                                              |
| Testing       | JUnit 5, Mockito, Instancio, Testcontainers, Rest Assured      |

> ℹ️ This is a backend-only project. Frontend will be developed in future phases.

---
Update database credentials in src/main/resources/application.properties as needed.

🔧 Docker support will be added in an upcoming release.


## Authentication & Authorization 

Login via POST /login

Authentication uses JWT

Authorization is role-based (STUDENT, TEACHER, PARENT, ADMINISTRATOR)

Access to endpoints is restricted based on authority levels


## Testing 
Unit tests for core logic using:

-JUnit 5

-Mockito

-Instancio for test data generation

Integration tests:

-Testcontainers

-Rest Assured

🧠 JaCoCo integration for code coverage reports is planned.


## Changelog 

All changes and version history are documented in CHANGELOG.md.


## License 

This project is licensed under the MIT License.

It is currently intended for educational and demonstration purposes.


## Author 

Created by  **Piotr Cisowski**

📧 **piotr.cisowski@hotmail.com**

This project is part of a personal portfolio and not affiliated with any institution.
