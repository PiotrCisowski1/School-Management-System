package com.cisowski.schoolmanagement.common.security.authorization.handler.impl.student;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.BaseResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import org.springframework.stereotype.Component;

@Component
public class StudentYearbookPermissionHandler extends BaseResourcePermissionHandler<YearbookEntity> {

    @Override
    public boolean canAccess(PermissionContext context, ResourceAccessContext accessContext) {
        DbLogger.info(buildLogMessage(context.getAction().name()));
        return switch (context.getAction()) {
            case READ -> handleSelfAccess(context, accessContext);
            default -> false;
        };
    }

    @Override
    public UserType getSupportedUserType() {
        return UserType.STUDENT;
    }

    @Override
    public ResourceType getSupportedResourceType() {
        return ResourceType.YEARBOOK;
    }

    private boolean handleSelfAccess(PermissionContext context, ResourceAccessContext accessContext) {
        StudentEntity accessingStudent = context.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY);
        if(accessingStudent == null)
            return false;
        Integer accessedYearbookId = accessContext.getAccessedMethodParameter("yearbookId");
        if(accessedYearbookId == null)
            return false;
        return accessingStudent.getYearbook().getId().equals(accessedYearbookId);
    }
}
