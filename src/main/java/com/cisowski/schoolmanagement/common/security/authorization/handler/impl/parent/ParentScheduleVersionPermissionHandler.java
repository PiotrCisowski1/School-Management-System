package com.cisowski.schoolmanagement.common.security.authorization.handler.impl.parent;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.BaseResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import org.springframework.stereotype.Component;

@Component
public class ParentScheduleVersionPermissionHandler extends BaseResourcePermissionHandler<ScheduleVersionEntity> {

    @Override
    public boolean canAccess(PermissionContext context, ResourceAccessContext accessContext) {
        DbLogger.info(buildLogMessage(context.getAction().name()));
        if (context.getAction().equals(ResourceActionType.READ))
            return checkParentChildRead(context, accessContext);
        return false;
    }

    @Override
    public UserType getSupportedUserType() {
        return UserType.PARENT;
    }

    @Override
    public ResourceType getSupportedResourceType() {
        return ResourceType.SCHEDULE_VERSION;
    }

    private boolean checkParentChildRead(PermissionContext context, ResourceAccessContext accessContext) {
        ParentEntity accessingParent = context.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY);
        if(accessingParent == null)
            return false;

        Integer yearbookIdParam = accessContext.getAccessedMethodParameter("yearbookId");
        if(yearbookIdParam != null)
            return isYearbookIdChildsYearbook(accessingParent, yearbookIdParam);

        Integer accessedScheduleVersion = accessContext.getAccessedMethodParameter("scheduleVersionId");
        if(accessedScheduleVersion != null) {
            ScheduleVersionEntity scheduleVersion = context.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY);
            return isParentsChildScheduleVersion(accessingParent, scheduleVersion);
        }
        return false;
    }

    private boolean isYearbookIdChildsYearbook(ParentEntity parent, Integer yearbookId) {
        return parent.getChildren().stream()
                .anyMatch(child -> child.getYearbook().getId().equals(yearbookId));
    }

    private boolean isParentsChildScheduleVersion(ParentEntity parent, ScheduleVersionEntity scheduleVersion){
        if(scheduleVersion != null){
            return parent.getChildren().stream()
                    .anyMatch(child -> scheduleVersion.getYearbook().getId().equals(child.getYearbook().getId()));
        }
        return false;
    }
}
