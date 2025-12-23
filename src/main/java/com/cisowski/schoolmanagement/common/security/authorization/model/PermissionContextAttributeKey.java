package com.cisowski.schoolmanagement.common.security.authorization.model;

import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import lombok.Getter;


@Getter
public enum PermissionContextAttributeKey {
    TEACHER_ENTITY(TeacherEntity.class),
    PARENT_ENTITY(ParentEntity.class),
    STUDENT_ENTITY(StudentEntity.class),
    SCHEDULE_VERSION_ENTITY(ScheduleVersionEntity.class);

    private final Class<?> expectedType;

    PermissionContextAttributeKey(Class<?> expectedType) {
        this.expectedType = expectedType;
    }

}
