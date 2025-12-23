package com.cisowski.schoolmanagement.common.security.authorization.handler.impl.parent;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.BaseResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceEntity;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.service.ScheduleOccurrenceService;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParentOccurrencePermissionHandler extends BaseResourcePermissionHandler<ScheduleOccurrenceEntity> {

    private final ScheduleOccurrenceService occurrenceService;

    @Override
    public boolean canAccess(PermissionContext context, ResourceAccessContext accessContext) {
        if(context.getAction().equals(ResourceActionType.READ))
            return handleChildRead(context, accessContext);
        return false;
    }

    @Override
    public UserType getSupportedUserType() {
        return UserType.PARENT;
    }

    @Override
    public ResourceType getSupportedResourceType() {
        return ResourceType.SCHEDULE_OCCURRENCE;
    }

    private boolean handleChildRead(PermissionContext context, ResourceAccessContext accessContext) {
        ParentEntity parent = context.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY);
        if(parent == null)
            return false;

        return handleSingleOccurrenceRead(parent, accessContext) ||
                handleReadByYearbook(parent, accessContext);
    }

    private boolean handleReadByYearbook(ParentEntity parent, ResourceAccessContext accessContext) {
        Integer yearbookId = accessContext.getAccessedMethodParameter("yearbookId");
        if(yearbookId == null)
            return false;

        return parent.getChildren().stream()
                .anyMatch(child -> child.getYearbook().getId().equals(yearbookId));
    }

    private boolean handleSingleOccurrenceRead(ParentEntity parent, ResourceAccessContext accessContext) {
        Long occurrenceId = accessContext.getAccessedMethodParameter("scheduleOccurrenceId");
        ScheduleOccurrenceEntity occurrence = occurrenceService.fetchScheduleOccurrence(occurrenceId);

        return parent.getChildren().stream()
                .anyMatch(child -> child.getYearbook().getId().equals(occurrence.getSchedule().getScheduleVersion().getYearbook().getId()));
    }
}
