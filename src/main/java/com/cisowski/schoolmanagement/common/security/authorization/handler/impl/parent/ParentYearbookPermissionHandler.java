package com.cisowski.schoolmanagement.common.security.authorization.handler.impl.parent;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.BaseResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import org.springframework.stereotype.Component;

@Component
public class ParentYearbookPermissionHandler extends BaseResourcePermissionHandler<YearbookEntity> {

    @Override
    public boolean canAccess(PermissionContext context, ResourceAccessContext accessContext) {
        DbLogger.info(buildLogMessage(context.getAction().name()));
        return switch (context.getAction()) {
            case READ -> handleParentChildRead(context, accessContext);
            default -> false;
        };
    }

    @Override
    public UserType getSupportedUserType() {
        return UserType.PARENT;
    }

    @Override
    public ResourceType getSupportedResourceType() {
        return ResourceType.YEARBOOK;
    }

    private boolean handleParentChildRead(PermissionContext context, ResourceAccessContext accessContext) {
        ParentEntity parent = context.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY);
        if (parent == null)
            return false;
        Integer yearbookId = accessContext.getAccessedMethodParameter("yearbookId");
        if(yearbookId == null)
            return false;

        return parent.getChildren().stream()
                .anyMatch(child -> child.getYearbook().getId().equals(yearbookId));
    }
}
