package com.cisowski.schoolmanagement.common.security.authorization.policy;

import com.cisowski.schoolmanagement.common.security.authorization.handler.ResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionHandlerKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.ScheduleVersionEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("SCHEDULE_VERSION")
public class ScheduleVersionAccessPolicy extends BaseResourceAccessPolicy<ScheduleVersionEntity> {

    public ScheduleVersionAccessPolicy(Map<PermissionHandlerKey, ResourcePermissionHandler<?>> handlers) {
        super(handlers);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.SCHEDULE_VERSION;
    }
}
