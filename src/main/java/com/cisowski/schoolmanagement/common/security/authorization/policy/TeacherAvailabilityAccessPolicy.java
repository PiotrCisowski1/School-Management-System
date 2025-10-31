package com.cisowski.schoolmanagement.common.security.authorization.policy;

import com.cisowski.schoolmanagement.common.security.authorization.handler.ResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionHandlerKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("TEACHER_AVAILABILITY")
public class TeacherAvailabilityAccessPolicy extends BaseResourceAccessPolicy<TeacherAvailabilityEntity> {


    public TeacherAvailabilityAccessPolicy(Map<PermissionHandlerKey, ResourcePermissionHandler<?>> handlers) {
        super(handlers);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.TEACHER_AVAILABILITY;
    }
}
