package com.cisowski.schoolmanagement.common.security.authorization.policy;

import com.cisowski.schoolmanagement.common.security.authorization.handler.ResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionHandlerKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("STUDENT")
public class StudentAccessPolicy extends BaseResourceAccessPolicy<SubjectEntity> {

    public StudentAccessPolicy(Map<PermissionHandlerKey, ResourcePermissionHandler<?>> handlers) {
        super(handlers);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.STUDENT;
    }
}
