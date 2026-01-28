erDiagram
      
"dbo.schedule_version" {
    int id "PK"
          datetime create_date ""
          bit is_active ""
          int yearbook_id "FK"
          varchar(20) name ""
          varchar(50) status ""
          
}
"dbo.schedules" {
    int id "PK"
          int schedule_version_id "FK"
          int subject_id "FK"
          int teacher_id "FK"
          int classroom_id "FK"
          int day_of_week ""
          time start_time ""
          time end_time ""
          varchar(15) recurrence_type ""
          varchar(50) status ""
          date effective_date ""
          date expiration_date ""
          
}
"dbo.schedule_change_log" {
    bigint id "PK"
          int schedule_id "FK"
          varchar(50) change_type ""
          varchar(50) field_name ""
          varchar(50) old_value ""
          varchar(50) new_value ""
          bigint changed_by_user_id ""
          date changed_at ""
          varchar(200) reason ""
          bit automatic_change ""
          
}
"dbo.schedule_changelog_affected_users" {
    int user_id "FK"
          bigint schedule_changelog_id "FK"
          
}
"dbo.app_config" {
    bigint id "PK"
          varchar(100) key ""
          varchar(150) value ""
          varchar(50) value_type ""
          varchar(200) description ""
          bit is_editable ""
          varchar(30) min_value ""
          varchar(30) max_value ""
          datetime created_at ""
          int modified_by "FK"
          datetime modified_at ""
          
}
"dbo.app_config_authorities" {
    varchar(100) app_config_key "FK"
          int authority_id "FK"
          
}
"dbo.schedule_occurrences" {
    bigint id "PK"
          int schedule_id ""
          datetime occurrence_date_time ""
          time occurrence_end_time ""
          varchar(30) status ""
          
}
"dbo.attendances" {
    bigint id "PK"
          int student_id "FK"
          varchar(30) attendance_status ""
          datetime created_at ""
          datetime last_modified_at ""
          int last_modified_by_user_id "FK"
          bigint occurrence_id "FK"
          
}
"dbo.flyway_schema_history" {
    int installed_rank "PK"
          nvarchar(50) version ""
          nvarchar(200) description ""
          nvarchar(20) type ""
          nvarchar(1000) script ""
          int checksum ""
          nvarchar(100) installed_by ""
          datetime installed_on ""
          int execution_time ""
          bit success ""
          
}
"dbo.logging_event" {
    decimal timestmp ""
          varchar(4000) formatted_message ""
          varchar(254) logger_name ""
          varchar(254) level_string ""
          varchar(254) thread_name ""
          smallint reference_flag ""
          varchar(254) arg0 ""
          varchar(254) arg1 ""
          varchar(254) arg2 ""
          varchar(254) arg3 ""
          varchar(254) caller_filename ""
          varchar(254) caller_class ""
          varchar(254) caller_method ""
          char(4) caller_line ""
          decimal event_id "PK"
          
}
"dbo.logging_event_exception" {
    decimal event_id "FK, PK"
          smallint i "PK"
          varchar(254) trace_line ""
          
}
"dbo.logging_event_property" {
    decimal event_id "FK, PK"
          varchar(254) mapped_key "PK"
          varchar(1024) mapped_value ""
          
}
"dbo.authorities" {
    int id "PK"
          varchar(30) authority ""
          
}
"dbo.address" {
    int id "PK"
          nvarchar(50) city ""
          nvarchar(50) street ""
          nvarchar(7) building_number ""
          nvarchar(80) voivodeship ""
          nvarchar(6) zip_code ""
          
}
"dbo.users" {
    int id "PK"
          varchar(150) email ""
          varchar(100) password ""
          bit enabled ""
          nvarchar(15) phone_number ""
          nvarchar(25) first_name ""
          nvarchar(50) last_name ""
          date birth_date ""
          nvarchar(10) gender ""
          int address_id "FK"
          datetime date_of_creation ""
          datetime date_of_update ""
          nvarchar(15) user_type ""
          
}
"dbo.users_authorities" {
    int user_id "FK"
          int authority_id "FK"
          
}
"dbo.subject_type" {
    int id "PK"
          nvarchar(20) name ""
          
}
"dbo.subjects" {
    int id "PK"
          nvarchar(20) name ""
          nvarchar(6) code ""
          nvarchar(150) description ""
          int type_id "FK"
          
}
"dbo.employees" {
    int user_id "FK, PK"
          date employment_start_date ""
          date employment_end_date ""
          
}
"dbo.teachers_details" {
    int user_id "FK, PK"
          
}
"dbo.teachers_availability" {
    int id "PK"
          int teacher_id "FK"
          int day_of_week ""
          time start_time ""
          time end_time ""
          bit is_available ""
          varchar(200) notes ""
          
}
"dbo.teachers_subjects" {
    int teacher_details_id "FK"
          int subject_id "FK"
          
}
"dbo.yearbooks" {
    int id "PK"
          int head_teacher_user_id "FK"
          nvarchar(7) symbol ""
          date starting_year ""
          date graduation_year ""
          
}
"dbo.yearbooks_subjects" {
    int yearbook_id "FK"
          int subject_id "FK"
          
}
"dbo.students_details" {
    int user_id "FK, PK"
          int yearbook_id "FK"
          date date_of_graduation ""
          
}
"dbo.parents_details" {
    int user_id "FK, PK"
          
}
"dbo.students_parents" {
    int parent_id "FK"
          int student_id "FK"
          
}
"dbo.equipments" {
    int id "PK"
          varchar(50) name ""
          
}
"dbo.classrooms" {
    int id "PK"
          varchar(50) name ""
          int capacity ""
          varchar(250) notes ""
          
}
"dbo.classroom_equipment" {
    int classroom_id "FK"
          int equipment_id "FK"
          int quantity ""
          
}
"dbo.grade_scales" {
    bigint id "PK"
          varchar(100) name ""
          varchar(250) description ""
          bit is_active ""
          datetime created_at ""
          datetime updated_at ""
          bit is_hide ""
          
}
"dbo.grade_values" {
    bigint id "PK"
          bigint grade_scale_id "FK"
          varchar(50) display_value ""
          int numeric_value ""
          varchar(200) description ""
          bit is_passing_grade ""
          bit is_hide ""
          
}
"dbo.grade_type" {
    bigint id "PK"
          varchar(50) grade_scope ""
          decimal weight ""
          
}
"dbo.grades" {
    bigint id "PK"
          int student_id "FK"
          int teacher_id "FK"
          int subject_id "FK"
          bigint grade_type_id "FK"
          datetime created_at ""
          nvarchar(500) comments ""
          bigint grade_value_id "FK"
          
}
      "dbo.schedule_version" ||--|{ "dbo.yearbooks": "id"
"dbo.schedules" ||--|{ "dbo.schedule_version": "id"
"dbo.schedules" ||--|{ "dbo.subjects": "id"
"dbo.schedules" ||--|{ "dbo.teachers_details": "user_id"
"dbo.schedules" ||--|{ "dbo.classrooms": "id"
"dbo.schedule_change_log" ||--|{ "dbo.schedules": "id"
"dbo.schedule_changelog_affected_users" ||--|{ "dbo.users": "id"
"dbo.schedule_changelog_affected_users" ||--|{ "dbo.schedule_change_log": "id"
"dbo.app_config" |o--|{ "dbo.users": "id"
"dbo.app_config_authorities" ||--|{ "dbo.app_config": "key"
"dbo.app_config_authorities" ||--|{ "dbo.authorities": "id"
"dbo.attendances" ||--|{ "dbo.students_details": "user_id"
"dbo.attendances" |o--|{ "dbo.users": "id"
"dbo.attendances" ||--|{ "dbo.schedule_occurrences": "id"
"dbo.logging_event_exception" ||--|{ "dbo.logging_event": "event_id"
"dbo.logging_event_property" ||--|{ "dbo.logging_event": "event_id"
"dbo.users" ||--|{ "dbo.address": "id"
"dbo.users_authorities" ||--|{ "dbo.users": "id"
"dbo.users_authorities" ||--|{ "dbo.authorities": "id"
"dbo.subjects" ||--|{ "dbo.subject_type": "id"
"dbo.employees" ||--|{ "dbo.users": "id"
"dbo.teachers_details" ||--|{ "dbo.users": "id"
"dbo.teachers_availability" ||--|{ "dbo.teachers_details": "user_id"
"dbo.teachers_subjects" ||--|{ "dbo.teachers_details": "user_id"
"dbo.teachers_subjects" ||--|{ "dbo.subjects": "id"
"dbo.yearbooks" ||--|{ "dbo.teachers_details": "user_id"
"dbo.yearbooks_subjects" ||--|{ "dbo.yearbooks": "id"
"dbo.yearbooks_subjects" ||--|{ "dbo.subjects": "id"
"dbo.students_details" ||--|{ "dbo.users": "id"
"dbo.students_details" ||--|{ "dbo.yearbooks": "id"
"dbo.parents_details" ||--|{ "dbo.users": "id"
"dbo.students_parents" ||--|{ "dbo.parents_details": "user_id"
"dbo.students_parents" ||--|{ "dbo.students_details": "user_id"
"dbo.classroom_equipment" ||--|{ "dbo.classrooms": "id"
"dbo.classroom_equipment" ||--|{ "dbo.equipments": "id"
"dbo.grade_values" ||--|{ "dbo.grade_scales": "id"
"dbo.grades" ||--|{ "dbo.students_details": "user_id"
"dbo.grades" ||--|{ "dbo.teachers_details": "user_id"
"dbo.grades" ||--|{ "dbo.subjects": "id"
"dbo.grades" ||--|{ "dbo.grade_type": "id"
"dbo.grades" ||--|{ "dbo.grade_values": "id"
