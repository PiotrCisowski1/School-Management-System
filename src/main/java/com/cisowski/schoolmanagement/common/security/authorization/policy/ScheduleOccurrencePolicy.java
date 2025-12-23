package com.cisowski.schoolmanagement.common.security.authorization.policy;

import com.cisowski.schoolmanagement.common.security.authorization.handler.ResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionHandlerKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("SCHEDULE_OCCURRENCE")
public class ScheduleOccurrencePolicy extends BaseResourceAccessPolicy<ScheduleOccurrenceEntity> {

    public ScheduleOccurrencePolicy(Map<PermissionHandlerKey, ResourcePermissionHandler<?>> handlers) {
        super(handlers);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.SCHEDULE_OCCURRENCE;
    }
}
