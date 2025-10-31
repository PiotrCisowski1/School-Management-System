package com.cisowski.schoolmanagement.common.security.authorization.handler.impl.parent;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.BaseResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import org.springframework.stereotype.Component;

@Component
public class ParentTeacherPermissionHandler extends BaseResourcePermissionHandler<TeacherEntity> {

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
        return UserType.PARENT;
    }

    @Override
    public ResourceType getSupportedResourceType() {
        return ResourceType.TEACHER;
    }

    private boolean handleReads(PermissionContext context, ResourceAccessContext accessContext) {
        ParentEntity parent = context.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY);
        if(parent == null)
            return false;
        Integer teacherId = accessContext.getAccessedMethodParameter("teacherId");
        if(teacherId == null)
            return false;
        return isChildsHeadTeacher(parent, teacherId) || isChildsSubjectTeacher(parent,teacherId);
    }

    private boolean isChildsHeadTeacher(ParentEntity parent, Integer teacherId) {
         return parent.getChildren().stream()
                .anyMatch(child -> child.getYearbook().getHeadTeacher().getId().equals(teacherId));
    }

    private boolean isChildsSubjectTeacher(ParentEntity parent, Integer teacherId) {
        return parent.getChildren().stream()
                .anyMatch(child -> child.getYearbook().getMainCourseSubjects().stream()
                        .anyMatch(subject -> subject.getTeachers().stream()
                                .anyMatch(teacher -> teacher.getId().equals(teacherId))));
    }
}
