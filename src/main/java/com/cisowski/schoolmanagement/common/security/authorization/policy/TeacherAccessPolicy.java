package com.cisowski.schoolmanagement.common.security.authorization.policy;

import com.cisowski.schoolmanagement.common.security.authorization.handler.ResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionHandlerKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("TEACHER")
public class TeacherAccessPolicy extends BaseResourceAccessPolicy<TeacherEntity>{

    public TeacherAccessPolicy(Map<PermissionHandlerKey, ResourcePermissionHandler<?>> handlers) {
        super(handlers);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.TEACHER;
    }
}
