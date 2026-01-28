# 🗄️ Data Model and Business Rules Documentation

This document describes the data model and entity relationships of the School Management System, as well as core business logic.

## Entity Relationship Diagram (ERD)

```mermaid
erDiagram
    %% Core User Management
    USERS ||--o| ADDRESS : "lives at"
    USERS ||--o{ USERS_AUTHORITIES : "has"
    USERS_AUTHORITIES }o--|| AUTHORITIES : "grants"
    USERS ||--o| EMPLOYEES : "is employee"
    USERS ||--o| TEACHERS_DETAILS : "is teacher"
    USERS ||--o| STUDENTS_DETAILS : "is student"
    USERS ||--o| PARENTS_DETAILS : "is parent"
    
    %% Teacher Related
    TEACHERS_DETAILS ||--o{ TEACHERS_SUBJECTS : "teaches"
    TEACHERS_SUBJECTS }o--|| SUBJECTS : "subject"
    TEACHERS_DETAILS ||--o{ TEACHERS_AVAILABILITY : "available"
    TEACHERS_DETAILS ||--o{ YEARBOOKS : "heads class"
    TEACHERS_DETAILS ||--o{ SCHEDULES : "teaches in"
    TEACHERS_DETAILS ||--o{ GRADES : "gives grades"
    
    %% Student Related
    STUDENTS_DETAILS }o--|| YEARBOOKS : "belongs to"
    STUDENTS_DETAILS ||--o{ STUDENTS_PARENTS : "has parent"
    STUDENTS_PARENTS }o--|| PARENTS_DETAILS : "parent of"
    STUDENTS_DETAILS ||--o{ GRADES : "receives"
    STUDENTS_DETAILS ||--o{ ATTENDANCES : "has attendance"
    
    %% Subject Related
    SUBJECTS }o--|| SUBJECT_TYPE : "of type"
    SUBJECTS ||--o{ YEARBOOKS_SUBJECTS : "taught in"
    YEARBOOKS_SUBJECTS }o--|| YEARBOOKS : "class"
    SUBJECTS ||--o{ SCHEDULES : "scheduled"
    SUBJECTS ||--o{ GRADES : "graded in"
    
    %% Schedule Related
    YEARBOOKS ||--o{ SCHEDULE_VERSION : "has versions"
    SCHEDULE_VERSION ||--o{ SCHEDULES : "contains"
    SCHEDULES }o--|| CLASSROOMS : "held in"
    SCHEDULES ||--o{ SCHEDULE_CHANGE_LOG : "has changes"
    SCHEDULES ||--o{ SCHEDULE_OCCURRENCES : "generates"
    SCHEDULE_CHANGE_LOG ||--o{ SCHEDULE_CHANGELOG_AFFECTED_USERS : "affects"
    SCHEDULE_CHANGELOG_AFFECTED_USERS }o--|| USERS : "user"
    SCHEDULE_OCCURRENCES ||--o{ ATTENDANCES : "tracked in"
    
    %% Classroom Related
    CLASSROOMS ||--o{ CLASSROOM_EQUIPMENT : "has"
    CLASSROOM_EQUIPMENT }o--|| EQUIPMENTS : "equipment"
    
    %% Grading System
    GRADE_SCALES ||--o{ GRADE_VALUES : "defines"
    GRADES }o--|| GRADE_VALUES : "uses value"
    GRADES }o--|| GRADE_TYPE : "of type"
    
    %% App Configuration
    APP_CONFIG ||--o{ APP_CONFIG_AUTHORITIES : "restricted by"
    APP_CONFIG_AUTHORITIES }o--|| AUTHORITIES : "authority"
    USERS ||--o{ APP_CONFIG : "modified by"
    
    %% Logging
    LOGGING_EVENT ||--o{ LOGGING_EVENT_EXCEPTION : "has exceptions"
    LOGGING_EVENT ||--o{ LOGGING_EVENT_PROPERTY : "has properties"
    
    %% Attendance
    ATTENDANCES }o--|| USERS : "modified by"

    USERS {
        int id PK
        varchar email
        varchar password
        bit enabled
        nvarchar phone_number
        nvarchar first_name
        nvarchar last_name
        date birth_date
        nvarchar gender
        int address_id FK
        datetime date_of_creation
        datetime date_of_update
        nvarchar user_type
    }
    
    ADDRESS {
        int id PK
        nvarchar city
        nvarchar street
        nvarchar building_number
        nvarchar voivodeship
        nvarchar zip_code
    }
    
    USERS_AUTHORITIES {
        int user_id FK
        int authority_id FK
    }
    
    AUTHORITIES {
        int id PK
        varchar authority
    }
    
    EMPLOYEES {
        int user_id FK_PK
        date employment_start_date
        date employment_end_date
    }
    
    TEACHERS_DETAILS {
        int user_id FK_PK
    }
    
    TEACHERS_SUBJECTS {
        int teacher_details_id FK
        int subject_id FK
    }
    
    TEACHERS_AVAILABILITY {
        int id PK
        int teacher_id FK
        int day_of_week
        time start_time
        time end_time
        bit is_available
        varchar notes
    }
    
    STUDENTS_DETAILS {
        int user_id FK_PK
        int yearbook_id FK
        date date_of_graduation
    }
    
    PARENTS_DETAILS {
        int user_id FK_PK
    }
    
    STUDENTS_PARENTS {
        int parent_id FK
        int student_id FK
    }
    
    SUBJECTS {
        int id PK
        nvarchar name
        nvarchar code
        nvarchar description
        int type_id FK
    }
    
    SUBJECT_TYPE {
        int id PK
        nvarchar name
    }
    
    YEARBOOKS {
        int id PK
        int head_teacher_user_id FK
        nvarchar symbol
        date starting_year
        date graduation_year
    }
    
    YEARBOOKS_SUBJECTS {
        int yearbook_id FK
        int subject_id FK
    }
    
    SCHEDULE_VERSION {
        int id PK
        datetime create_date
        bit is_active
        int yearbook_id FK
        varchar name
        varchar status
    }
    
    SCHEDULES {
        int id PK
        int schedule_version_id FK
        int subject_id FK
        int teacher_id FK
        int classroom_id FK
        int day_of_week
        time start_time
        time end_time
        varchar recurrence_type
        varchar status
        date effective_date
        date expiration_date
    }
    
    SCHEDULE_CHANGE_LOG {
        bigint id PK
        int schedule_id FK
        varchar change_type
        varchar field_name
        varchar old_value
        varchar new_value
        bigint changed_by_user_id
        date changed_at
        varchar reason
        bit automatic_change
    }
    
    SCHEDULE_CHANGELOG_AFFECTED_USERS {
        int user_id FK
        bigint schedule_changelog_id FK
    }
    
    SCHEDULE_OCCURRENCES {
        bigint id PK
        int schedule_id FK
        datetime occurrence_date_time
        time occurrence_end_time
        varchar status
    }
    
    CLASSROOMS {
        int id PK
        varchar name
        int capacity
        varchar notes
    }
    
    EQUIPMENTS {
        int id PK
        varchar name
    }
    
    CLASSROOM_EQUIPMENT {
        int classroom_id FK
        int equipment_id FK
        int quantity
    }
    
    ATTENDANCES {
        bigint id PK
        int student_id FK
        varchar attendance_status
        datetime created_at
        datetime last_modified_at
        int last_modified_by_user_id FK
        bigint occurrence_id FK
    }
    
    GRADES {
        bigint id PK
        int student_id FK
        int teacher_id FK
        int subject_id FK
        bigint grade_type_id FK
        datetime created_at
        nvarchar comments
        bigint grade_value_id FK
    }
    
    GRADE_SCALES {
        bigint id PK
        varchar name
        varchar description
        bit is_active
        datetime created_at
        datetime updated_at
        bit is_hide
    }
    
    GRADE_VALUES {
        bigint id PK
        bigint grade_scale_id FK
        varchar display_value
        int numeric_value
        varchar description
        bit is_passing_grade
        bit is_hide
    }
    
    GRADE_TYPE {
        bigint id PK
        varchar grade_scope
        decimal weight
    }
    
    APP_CONFIG {
        bigint id PK
        varchar key
        varchar value
        varchar value_type
        varchar description
        bit is_editable
        varchar min_value
        varchar max_value
        datetime created_at
        int modified_by FK
        datetime modified_at
    }
    
    APP_CONFIG_AUTHORITIES {
        varchar app_config_key FK
        int authority_id FK
    }
    
    LOGGING_EVENT {
        decimal event_id PK
        decimal timestmp
        varchar formatted_message
        varchar logger_name
        varchar level_string
        varchar thread_name
        smallint reference_flag
        varchar arg0
        varchar arg1
        varchar arg2
        varchar arg3
        varchar caller_filename
        varchar caller_class
        varchar caller_method
        char caller_line
    }
    
    LOGGING_EVENT_EXCEPTION {
        decimal event_id FK_PK
        smallint i PK
        varchar trace_line
    }
    
    LOGGING_EVENT_PROPERTY {
        decimal event_id FK_PK
        varchar mapped_key PK
        varchar mapped_value
    }
    
    FLYWAY_SCHEMA_HISTORY {
        int installed_rank PK
        nvarchar version
        nvarchar description
        nvarchar type
        nvarchar script
        int checksum
        nvarchar installed_by
        datetime installed_on
        int execution_time
        bit success
    }
```

---

## 📋 Table of Contents

- [Core Entities](#core-entities)
  - [Users](#users)
  - [Address](#address)
  - [Authorities](#authorities)
- [Academic Entities](#academic-entities)
  - [Subjects](#subjects)
  - [Yearbooks](#yearbooks)
  - [Grades](#grades)
- [Personnel Entities](#personnel-entities)
  - [Teachers](#teachers)
  - [Students](#students)
  - [Parents](#parents)
  - [Employees](#employees)
- [Schedule Management](#schedule-management)
  - [Schedules](#schedules)
  - [Schedule Versions](#schedule-versions)
  - [Schedule Change Log](#schedule-change-log)
- [Facility Management](#facility-management)
  - [Classrooms](#classrooms)
  - [Equipment](#equipment)
- [Attendance System](#attendance-system)
- [Grading System](#grading-system)
- [System Configuration](#system-configuration)
- [Logging System](#logging-system)

---

## Core Entities

### Users
**Table:** `dbo.users`

Central entity storing all system users including teachers, students, parents, and employees.

**Fields:**
- `id` (PK): Unique user identifier
- `email`: User email address (unique)
- `password`: Encrypted password
- `enabled`: Account active status
- `phone_number`: Contact phone number
- `first_name`: User's first name
- `last_name`: User's last name
- `birth_date`: Date of birth
- `gender`: User gender
- `address_id` (FK): Reference to address
- `date_of_creation`: Account creation timestamp
- `date_of_update`: Last update timestamp
- `user_type`: Type of user (teacher/student/parent)

**Relationships:**
- One-to-One with Address
- One-to-Many with Users_Authorities
- One-to-One with Teachers_Details, Students_Details, Parents_Details (depending on user_type)

**Business Rules:**
- Email must be unique
- User type determines which detail table is populated

---

### Address
**Table:** `dbo.address`

Stores physical address information for users.

**Fields:**
- `id` (PK): Unique address identifier
- `city`: City name
- `street`: Street name
- `building_number`: Building/house number
- `voivodeship`: Province/state
- `zip_code`: Postal code

**Relationships:**
- One-to-Many with Users

**Business Rules:**
- Cannot be shared by multiple users
- All fields are required for complete address

---

### Authorities
**Table:** `dbo.authorities`

Defines system roles.

**Fields:**
- `id` (PK): Unique authority identifier
- `authority`: Authority name (e.g., ADMIN, TEACHER)

**Relationships:**
- Many-to-Many with Users through Users_Authorities
- Many-to-Many with App_Config through App_Config_Authorities

**Business Rules:**
- Standard Spring Security authority format
- Multiple authorities can be assigned to single user
- Used for authorization across the system

---

## Academic Entities

### Subjects
**Table:** `dbo.subjects`

Defines academic subjects taught in the school.

**Fields:**
- `id` (PK): Unique subject identifier
- `name`: Subject name
- `code`: Short subject code
- `description`: Detailed description
- `type_id` (FK): Reference to subject type

**Related Tables:**
- `dbo.subject_type`: Categories of subjects (mandatory, elective, etc.)

**Relationships:**
- Many-to-One with Subject_Type
- Many-to-Many with Teachers through Teachers_Subjects
- Many-to-Many with Yearbooks through Yearbooks_Subjects
- One-to-Many with Schedules
- One-to-Many with Grades

**Business Rules:**
- Subject code must be unique
- Type determines scheduling requirements
- Can be assigned to multiple yearbooks

---

### Yearbooks
**Table:** `dbo.yearbooks`

Represents classes of students progressing through school years together.

**Fields:**
- `id` (PK): Unique yearbook identifier
- `head_teacher_user_id` (FK): Head teacher (form tutor)
- `symbol`: Class symbol/code (e.g., "1A", "2B")
- `starting_year`: Year the class started
- `graduation_year`: Expected graduation year

**Relationships:**
- Many-to-One with Teachers_Details (head teacher)
- One-to-Many with Students_Details
- Many-to-Many with Subjects through Yearbooks_Subjects
- One-to-Many with Schedule_Version

**Business Rules:**
- Each yearbook must have one head teacher
- Symbol should be unique
- Defines curriculum through yearbooks_subjects

---

### Grades
**Table:** `dbo.grades`

Records student grades/marks for subjects.

**Fields:**
- `id` (PK): Unique grade identifier
- `student_id` (FK): Reference to student
- `teacher_id` (FK): Teacher who assigned the grade
- `subject_id` (FK): Subject being graded
- `grade_type_id` (FK): Type/category of grade
- `created_at`: When grade was assigned
- `comments`: Additional comments
- `grade_value_id` (FK): The actual grade value

**Related Tables:**
- `dbo.grade_scales`: Defines grading scales (numeric, letter, etc.)
- `dbo.grade_values`: Grade values within a scale, used globally by teachers
- `dbo.grade_type`: Categories (quiz, exam, homework) with weights

**Relationships:**
- Many-to-One with Students_Details
- Many-to-One with Teachers_Details
- Many-to-One with Subjects
- Many-to-One with Grade_Type
- Many-to-One with Grade_Values

**Business Rules:**
- Grade value must belong to active scale
- Only teacher of the subject can assign grades
- Timestamps track grade history
- Comments are optional but recommended

---

## Personnel Entities

### Teachers
**Tables:** `dbo.teachers_details`, `dbo.teachers_subjects`, `dbo.teachers_availability`

Extended information for users with teacher role.

**teachers_details Fields:**
- `user_id` (FK, PK): Reference to users table

**teachers_subjects Fields:**
- `teacher_details_id` (FK): Reference to teacher
- `subject_id` (FK): Subject the teacher can teach

**teachers_availability Fields:**
- `id` (PK): Unique availability record
- `teacher_id` (FK): Reference to teacher
- `day_of_week`: Day (1-7)
- `start_time`: Availability start
- `end_time`: Availability end
- `is_available`: Available/unavailable flag
- `notes`: Additional notes

**Relationships:**
- One-to-One with Users
- Many-to-Many with Subjects
- One-to-Many with Teachers_Availability
- One-to-Many with Schedules
- One-to-Many with Yearbooks (as head teacher)
- One-to-Many with Grades

**Business Rules:**
- Teacher can teach multiple subjects
- Availability used for schedule optimization
- Can be head teacher of one yearbook only
- Must have at least one subject assigned

---

### Students
**Tables:** `dbo.students_details`, `dbo.students_parents`

Extended information for users with student role.

**students_details Fields:**
- `user_id` (FK, PK): Reference to users table
- `yearbook_id` (FK): Class the student belongs to
- `date_of_graduation`: Expected graduation date

**students_parents Fields:**
- `parent_id` (FK): Reference to parent
- `student_id` (FK): Reference to student

**Relationships:**
- One-to-One with Users
- Many-to-One with Yearbooks
- Many-to-Many with Parents_Details
- One-to-Many with Grades
- One-to-Many with Attendances

**Business Rules:**
- Student must belong to exactly one yearbook
- Can have multiple parents/guardians
- Grades and attendance tracked per student

---

### Parents
**Table:** `dbo.parents_details`

Extended information for users with parent/guardian role.

**Fields:**
- `user_id` (FK, PK): Reference to users table

**Relationships:**
- One-to-One with Users
- Many-to-Many with Students_Details through Students_Parents

**Business Rules:**
- Can be guardian of multiple students
- Access to child's grades and attendance

---

### Employees
**Table:** `dbo.employees`

Extended information for school staff (non-teaching personnel).

**Fields:**
- `user_id` (FK, PK): Reference to users table
- `employment_start_date`: Employment start date
- `employment_end_date`: Employment end date (null if active)

**Relationships:**
- One-to-One with Users

**Business Rules:**
- Tracks employment period
- Separate from teaching staff
- Can have administrative authorities

---

## Schedule Management

### Schedules
**Table:** `dbo.schedules`

Defines recurring class schedules.

**Fields:**
- `id` (PK): Unique schedule identifier
- `schedule_version_id` (FK): Version this schedule belongs to
- `subject_id` (FK): Subject being taught
- `teacher_id` (FK): Teacher conducting the class
- `classroom_id` (FK): Room where class is held
- `day_of_week`: Day (1=Monday, 7=Sunday)
- `start_time`: Class start time
- `end_time`: Class end time
- `recurrence_type`: Pattern (weekly, biweekly, etc.)
- `status`: Active, cancelled, pending
- `effective_date`: When schedule becomes active
- `expiration_date`: When schedule expires

**Relationships:**
- Many-to-One with Schedule_Version
- Many-to-One with Subjects
- Many-to-One with Teachers_Details
- Many-to-One with Classrooms
- One-to-Many with Schedule_Change_Log
- One-to-Many with Schedule_Occurrences

**Business Rules:**
- Must not conflict with teacher availability
- Recurrence generates specific occurrences
- Changes tracked in change log

---

### Schedule Versions
**Table:** `dbo.schedule_version`

Manages different versions of class schedules for a yearbook.

**Fields:**
- `id` (PK): Unique version identifier
- `create_date`: Version creation date
- `is_active`: Currently active version flag
- `yearbook_id` (FK): Yearbook this schedule belongs to
- `name`: Version name/description
- `status`: Draft, active, archived

**Relationships:**
- Many-to-One with Yearbooks
- One-to-Many with Schedules

**Business Rules:**
- Only one active version per yearbook at a time
- Allows schedule changes without losing history
- Previous versions remain for audit trail

---

### Schedule Change Log
**Tables:** `dbo.schedule_change_log`, `dbo.schedule_changelog_affected_users`

Tracks all changes made to schedules.

**schedule_change_log Fields:**
- `id` (PK): Unique change record
- `schedule_id` (FK): Schedule that was changed
- `change_type`: Type of change (create, update, delete)
- `field_name`: Field that changed
- `old_value`: Previous value
- `new_value`: New value
- `changed_by_user_id`: User who made the change
- `changed_at`: When change occurred
- `reason`: Reason for change
- `automatic_change`: System vs. manual change

**schedule_changelog_affected_users Fields:**
- `user_id` (FK): Affected user
- `schedule_changelog_id` (FK): Change record

**Relationships:**
- Many-to-One with Schedules
- Many-to-Many with Users (affected users)

**Business Rules:**
- All schedule modifications logged
- Affected users notified of changes
- Audit trail for accountability

---

### Schedule Occurrences
**Table:** `dbo.schedule_occurrences`

Specific instances of scheduled classes.

**Fields:**
- `id` (PK): Unique occurrence identifier
- `schedule_id` (FK): Parent schedule
- `occurrence_date_time`: Specific date and time
- `occurrence_end_time`: End time for this occurrence
- `status`: Scheduled, completed, cancelled

**Relationships:**
- Many-to-One with Schedules
- One-to-Many with Attendances

**Business Rules:**
- Generated from schedule recurrence pattern
- Can be individually cancelled or rescheduled
- Used for attendance tracking

---

## Facility Management

### Classrooms
**Table:** `dbo.classrooms`

Physical rooms where classes are held.

**Fields:**
- `id` (PK): Unique classroom identifier
- `name`: Room name/number
- `capacity`: Maximum student capacity
- `notes`: Additional information

**Relationships:**
- One-to-Many with Schedules
- Many-to-Many with Equipments through Classroom_Equipment

**Business Rules:**
- Availability tracked for scheduling conflicts

---

### Equipment
**Tables:** `dbo.equipments`, `dbo.classroom_equipment`

Equipment available in classrooms.

**equipments Fields:**
- `id` (PK): Unique equipment identifier
- `name`: Equipment name (projector, computer, etc.)

**classroom_equipment Fields:**
- `classroom_id` (FK): Classroom
- `equipment_id` (FK): Equipment type
- `quantity`: Number of items

**Relationships:**
- Many-to-Many with Classrooms

**Business Rules:**
- Subjects can require specific equipment
- Scheduling considers equipment needs
- Quantity tracked per classroom

---

## Attendance System

### Attendances
**Table:** `dbo.attendances`

Records student attendance for schedule occurrences.

**Fields:**
- `id` (PK): Unique attendance record
- `student_id` (FK): Student
- `attendance_status`: Present, absent, late, excused
- `created_at`: When attendance was recorded
- `last_modified_at`: Last modification time
- `last_modified_by_user_id` (FK): User who last modified
- `occurrence_id` (FK): Specific class occurrence

**Relationships:**
- Many-to-One with Students_Details
- Many-to-One with Schedule_Occurrences
- Many-to-One with Users (modifier)

**Business Rules:**
- One record per student per occurrence
- Status affects student standing

---

## Grading System

### Grade Scales
**Table:** `dbo.grade_scales`

Defines different grading scales used in the system.

**Fields:**
- `id` (PK): Unique scale identifier
- `name`: Scale name (e.g., "Standard 1-6", "Pass/Fail")
- `description`: Detailed description
- `is_active`: Currently used flag
- `created_at`: Creation timestamp
- `updated_at`: Last update timestamp
- `is_hide`: Hide from UI flag

**Relationships:**
- One-to-Many with Grade_Values

**Business Rules:**
- Multiple scales can exist
- Only active scales available for new grades
- Cannot delete scale with existing grades

---

### Grade Values
**Table:** `dbo.grade_values`

Individual values within a grading scale.

**Fields:**
- `id` (PK): Unique value identifier
- `grade_scale_id` (FK): Parent scale
- `display_value`: What shows to users (A, B, 5, Pass)
- `numeric_value`: Numeric equivalent for calculations
- `description`: What the grade means
- `is_passing_grade`: Whether this grade passes
- `is_hide`: Hide from UI flag

**Relationships:**
- Many-to-One with Grade_Scales
- One-to-Many with Grades

**Business Rules:**
- Display values unique within scale
- Numeric values used for GPA calculations
- Passing grades determine promotion

---

### Grade Types
**Table:** `dbo.grade_type`

Categories of grades with different weights.

**Fields:**
- `id` (PK): Unique type identifier
- `grade_scope`: Type name (quiz, midterm, final, homework)
- `weight`: Importance weight (e.g., 0.1, 0.3)

**Relationships:**
- One-to-Many with Grades

**Business Rules:**
- Weights used for final grade calculation
- Scope determines when grade can be assigned
- Different subjects can use different types

---

## System Configuration

### App Config
**Tables:** `dbo.app_config`, `dbo.app_config_authorities`

System-wide configuration parameters.

**app_config Fields:**
- `id` (PK): Unique config identifier
- `key`: Configuration key
- `value`: Configuration value
- `value_type`: Data type (string, int, boolean)
- `description`: What this config does
- `is_editable`: Can be changed via UI
- `min_value`: Minimum allowed value
- `max_value`: Maximum allowed value
- `created_at`: Creation timestamp
- `modified_by` (FK): User who last modified
- `modified_at`: Last modification time

**app_config_authorities Fields:**
- `app_config_key` (FK): Configuration key
- `authority_id` (FK): Required authority to modify

**Relationships:**
- Many-to-One with Users (modifier)
- Many-to-Many with Authorities

**Business Rules:**
- Keys must be unique
- Type validation enforced
- Authority required for sensitive configs

---

## Logging System

### Logging Event
**Tables:** `dbo.logging_event`, `dbo.logging_event_exception`, `dbo.logging_event_property`

Application logging system (typically used by Logback/SLF4J).

**logging_event Fields:**
- `event_id` (PK): Unique log event
- `timestmp`: When event occurred
- `formatted_message`: Log message
- `logger_name`: Logger that created event
- `level_string`: Log level (DEBUG, INFO, WARN, ERROR)
- `thread_name`: Thread name
- `caller_filename`: Source file
- `caller_class`: Source class
- `caller_method`: Source method
- `caller_line`: Line number

**logging_event_exception Fields:**
- `event_id` (FK, PK): Parent event
- `i` (PK): Line number in stack trace
- `trace_line`: Stack trace line

**logging_event_property Fields:**
- `event_id` (FK, PK): Parent event
- `mapped_key` (PK): Property name
- `mapped_value`: Property value

**Relationships:**
- One-to-Many with Logging_Event_Exception
- One-to-Many with Logging_Event_Property

**Business Rules:**
- Automatic logging by application
- Stack traces stored for exceptions
- MDC properties stored separately
- Used for debugging and monitoring

---

## Database Migration

### Flyway Schema History
**Table:** `dbo.flyway_schema_history`

Tracks database schema migrations (managed by Flyway).

**Fields:**
- `installed_rank` (PK): Order of execution
- `version`: Migration version number
- `description`: Migration description
- `type`: SQL or Java migration
- `script`: Script filename
- `checksum`: Script validation checksum
- `installed_by`: User who ran migration
- `installed_on`: When migration ran
- `execution_time`: How long it took (ms)
- `success`: Whether migration succeeded

**Business Rules:**
- Managed automatically by Flyway
- Do not modify manually
- Ensures consistent database state
- Failed migrations block startup

---

## Key Design Patterns

### 1. **User Type Hierarchy**
- Central `users` table with `user_type` discriminator
- Extended by detail tables (teachers_details, students_details, etc.)
- Allows shared authentication while maintaining role-specific data

### 2. **Many-to-Many Relationships**
- Junction tables connect entities (teachers_subjects, yearbooks_subjects, etc.)
- Allows flexible associations without data duplication

### 3. **Versioning & History**
- Schedule versions allow changes without data loss
- Change logs provide audit trails
- Soft deletes via status fields

### 4. **Flexible Grading**
- Multiple grade scales supported
- Weighted grade types for calculations
- Passing thresholds configurable

### 5. **Temporal Validity**
- Effective/expiration dates on schedules
- Employment periods tracked
- Historical data preserved

### 6. **Configuration Management**
- Authority-based access to sensitive configs
- Type validation and bounds checking
- Audit trail via modified_by/modified_at

---

*Last Updated: 2026-01-28*
