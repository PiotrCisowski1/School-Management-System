package com.cisowski.schoolmanagement.common.security.authorization.enricher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.schedule.service.ScheduleVersionService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BasePermissionContextEnricher implements PermissionContextEnricher {

    private final ScheduleVersionService scheduleVersionService;

    public void addScheduleVersionToContext(PermissionContext context, ResourceAccessContext accessContext){
        Integer scheduleVersionId = accessContext.getAccessedMethodParameter("scheduleVersionId");
        ScheduleVersionEntity accessedScheduleVersion = scheduleVersionService.fetchScheduleVersion(scheduleVersionId);
        context.putAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY, accessedScheduleVersion);
    }
}
