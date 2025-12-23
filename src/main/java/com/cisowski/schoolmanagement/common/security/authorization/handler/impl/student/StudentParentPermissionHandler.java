package com.cisowski.schoolmanagement.common.security.authorization.handler.impl.student;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.BaseResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentParentPermissionHandler extends BaseResourcePermissionHandler<ParentEntity> {

    @Override
    public boolean canAccess(PermissionContext context, ResourceAccessContext accessContext) {
        DbLogger.info(buildLogMessage(context.getAction().name()));
        return switch (context.getAction()) {
            case READ -> handleReadParent(context, accessContext);
            default -> false;
        };
    }

    @Override
    public UserType getSupportedUserType() {
        return UserType.STUDENT;
    }

    @Override
    public ResourceType getSupportedResourceType() {
        return ResourceType.PARENT;
    }

    private boolean handleReadParent(PermissionContext context, ResourceAccessContext accessContext) {
        StudentEntity student = context.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY);
        if(student == null)
            return false;
        Integer parentId = accessContext.getAccessedMethodParameter("parentId");
        if(parentId == null)
            return false;

        return student.getParents().stream()
                .anyMatch(parent -> parent.getId().equals(parentId));
    }
}
