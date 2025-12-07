package com.cisowski.schoolmanagement.common.security.authorization.handler.impl.parent;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.BaseResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import org.springframework.stereotype.Component;

@Component
public class ParentSchedulePermissionHandler extends BaseResourcePermissionHandler<ScheduleEntity> {

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
        return ResourceType.SCHEDULE;
    }

    private boolean handleParentChildRead(PermissionContext context, ResourceAccessContext accessContext) {
        ParentEntity accessingParent = context.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY);
        if (accessingParent == null)
            return false;
        ScheduleVersionEntity accessedScheduleVersion = context.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY);
        if (accessedScheduleVersion == null)
            return false;

        Integer accessedScheduleId = accessContext.getAccessedMethodParameter("scheduleId");
        if (accessedScheduleId != null)
            return isParentsChildScheduleAttendant(accessingParent, accessedScheduleVersion) &&
                    isScheduleInChildsScheduleVersion(accessedScheduleId, accessedScheduleVersion);
        Integer dayOfWeek = accessContext.getAccessedMethodParameter("dayOfWeek");
        if (dayOfWeek != null)
            return isParentsChildScheduleAttendant(accessingParent, accessedScheduleVersion);

        return false;
    }

    private boolean isParentsChildScheduleAttendant(ParentEntity parent, ScheduleVersionEntity scheduleVersion) {
        return parent.getChildren().stream()
                .anyMatch(child -> scheduleVersion.getYearbook().getId().equals(child.getYearbook().getId()));
    }

    private boolean isScheduleInChildsScheduleVersion(Integer scheduleId, ScheduleVersionEntity scheduleVersion) {
        return scheduleVersion.getSchedules().stream()
                .anyMatch(schedule -> schedule.getId().equals(scheduleId));
    }
}
