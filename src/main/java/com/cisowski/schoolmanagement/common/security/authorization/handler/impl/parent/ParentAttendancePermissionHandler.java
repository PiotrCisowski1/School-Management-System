package com.cisowski.schoolmanagement.common.security.authorization.handler.impl.parent;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.BaseResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceEntity;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class ParentAttendancePermissionHandler extends BaseResourcePermissionHandler<AttendanceEntity> {

    @Override
    public boolean canAccess(PermissionContext context, ResourceAccessContext accessContext) {
        if(context.getAction().equals(ResourceActionType.READ))
            return checkReadPermission(context, accessContext);
        return false;
    }

    @Override
    public UserType getSupportedUserType() {
        return UserType.PARENT;
    }

    @Override
    public ResourceType getSupportedResourceType() {
        return ResourceType.ATTENDANCE;
    }

    private boolean checkReadPermission(PermissionContext context, ResourceAccessContext accessContext) {
        ParentEntity parent = context.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY);
        if(parent == null)
            return false;

        Integer studentId = accessContext.getAccessedMethodParameter("studentId");
        if(studentId != null)
            return !CollectionUtils.isEmpty(parent.getChildren()) && parent.getChildren().stream()
                    .anyMatch(child -> child.getId().equals(studentId));

        return false;
    }
}
