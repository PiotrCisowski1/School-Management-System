package com.cisowski.schoolmanagement.common.security.authorization.handler.impl.teacher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.BaseResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeacherParentPermissionHandler extends BaseResourcePermissionHandler<ParentEntity> {

    @Override
    public boolean canAccess(PermissionContext context, ResourceAccessContext accessContext) {
        DbLogger.info(buildLogMessage(context.getAction().name()));
        return switch (context.getAction()) {
            case READ -> handleTeacherReadParent(context, accessContext);
            default -> false;
        };
    }

    @Override
    public UserType getSupportedUserType() {
        return UserType.TEACHER;
    }

    @Override
    public ResourceType getSupportedResourceType() {
        return ResourceType.PARENT;
    }

    private boolean handleTeacherReadParent(PermissionContext context, ResourceAccessContext accessContext){
        TeacherEntity teacher = context.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY);
        if(teacher == null)
            return false;
        Integer parentId = accessContext.getAccessedMethodParameter("parentId");
        if(parentId == null)
            return false;

        if(teacher.getLeadingYearbook() != null)
            return teacher.getLeadingYearbook().getStudentsInYearbook().stream()
                    .anyMatch(student -> student.getParents().stream()
                            .anyMatch(parent -> parent.getId().equals(parentId)));

        return false;
    }
}
