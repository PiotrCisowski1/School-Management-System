package com.cisowski.schoolmanagement.common.security.authorization.handler.impl.teacher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.BaseResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeacherStudentPermissionHandler extends BaseResourcePermissionHandler<StudentEntity> {

    @Override
    public boolean canAccess(PermissionContext context, ResourceAccessContext accessContext) {
        DbLogger.info(buildLogMessage(context.getAction().toString()));
        return switch (context.getAction()) {
            case READ -> handleReads(context, accessContext);
            default -> false;
        };
    }

    @Override
    public UserType getSupportedUserType() {
        return UserType.TEACHER;
    }

    @Override
    public ResourceType getSupportedResourceType() {
        return ResourceType.STUDENT;
    }

    private boolean handleReads(PermissionContext context, ResourceAccessContext accessContext) {
        TeacherEntity teacher = context.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY);
        if (teacher == null)
            return false;
        Integer studentId = accessContext.getAccessedMethodParameter("studentId");
        if (studentId == null)
            return false;
        if (handleTeacherTeachingSubjectOfStudent(teacher, studentId))
            return true;
        return handleHeadTeacherOfStudent(teacher, studentId);
    }

    private boolean handleHeadTeacherOfStudent(TeacherEntity teacher, Integer studentId) {
        if(teacher.getLeadingYearbook() != null)
            return teacher.getLeadingYearbook().getStudentsInYearbook().stream()
                    .anyMatch(student -> student.getId().equals(studentId));
        return false;
    }

    private boolean handleTeacherTeachingSubjectOfStudent(TeacherEntity teacher, Integer studentId) {
        return teacher.getTeachingSubjects().stream()
                .anyMatch(subject -> subject.getYearbooksTakingSubject().stream()
                        .anyMatch(yearbook -> yearbook.getStudentsInYearbook().stream()
                                .anyMatch(student -> student.getId().equals(studentId))));
    }
}
