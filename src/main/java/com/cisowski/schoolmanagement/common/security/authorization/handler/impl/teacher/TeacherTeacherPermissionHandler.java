package com.cisowski.schoolmanagement.common.security.authorization.handler.impl.teacher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.BaseResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import org.springframework.stereotype.Component;

@Component
public class TeacherTeacherPermissionHandler extends BaseResourcePermissionHandler<TeacherEntity> {

    @Override
    public boolean canAccess(PermissionContext context, ResourceAccessContext accessContext) {
        DbLogger.info(buildLogMessage(context.getAction().name()));
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
        return ResourceType.TEACHER;
    }

    private boolean handleReads(PermissionContext context, ResourceAccessContext accessContext) {
        TeacherEntity accessingTeacher = context.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY);
        if (accessingTeacher == null)
            return false;
        Integer teacherId = accessContext.getAccessedMethodParameter("teacherId");
        if (teacherId == null)
            return false;

        return accessingTeacher.getId().equals(teacherId) ||
                areCorelatedWithSameYearbook(accessingTeacher, teacherId);

    }

    private boolean areCorelatedWithSameYearbook(TeacherEntity accessingTeacher, Integer teacherId) {
        return accessingTeacher.getLeadingYearbook() != null &&
                accessingTeacher.getLeadingYearbook().getMainCourseSubjects().stream()
                .anyMatch(subject -> subject.getTeachers().stream()
                        .anyMatch(teacher -> teacher.getId().equals(teacherId)));
    }
}
