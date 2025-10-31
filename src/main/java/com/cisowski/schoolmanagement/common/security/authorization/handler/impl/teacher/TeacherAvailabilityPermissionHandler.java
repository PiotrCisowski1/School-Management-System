package com.cisowski.schoolmanagement.common.security.authorization.handler.impl.teacher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.BaseResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityEntity;
import org.springframework.stereotype.Component;

@Component
public class TeacherAvailabilityPermissionHandler extends BaseResourcePermissionHandler<TeacherAvailabilityEntity> {

    @Override
    public boolean canAccess(PermissionContext context, ResourceAccessContext accessContext) {
        DbLogger.info(buildLogMessage(context.getAction().name()));
        return switch (context.getAction()) {
            case CREATE, DELETE, READ -> handleSelfAccess(context, accessContext);
            default -> false;
        };
    }

    @Override
    public UserType getSupportedUserType() {
        return UserType.TEACHER;
    }

    @Override
    public ResourceType getSupportedResourceType() {
        return ResourceType.TEACHER_AVAILABILITY;
    }

    private boolean handleSelfAccess(PermissionContext context, ResourceAccessContext accessContext) {
        TeacherEntity teacher = context.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY);
        if (teacher == null)
            return false;
        Integer teacherId = accessContext.getAccessedMethodParameter("teacherId");
        if (teacherId == null)
            return false;

        return isSameTeacher(teacher, teacherId);
    }

    private boolean isSameTeacher(TeacherEntity teacher, Integer teacherId) {
        return teacher.getId().equals(teacherId);
    }
}
