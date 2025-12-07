package com.cisowski.schoolmanagement.common.security.authorization.policy;

import com.cisowski.schoolmanagement.common.security.authorization.handler.ResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionHandlerKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("ATTENDANCE")
public class AttendanceAccessPolicy extends BaseResourceAccessPolicy<AttendanceEntity> {

    public AttendanceAccessPolicy(Map<PermissionHandlerKey, ResourcePermissionHandler<?>> handlers) {
        super(handlers);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.ATTENDANCE;
    }
}
