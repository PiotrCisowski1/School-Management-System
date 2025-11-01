package com.cisowski.schoolmanagement.common.security.authorization.policy;

import com.cisowski.schoolmanagement.common.security.authorization.handler.ResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.*;
import com.cisowski.schoolmanagement.grade.model.grade.GradeEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("GRADE")
public class GradeAccessPolicy extends BaseResourceAccessPolicy<GradeEntity> {

    public GradeAccessPolicy(Map<PermissionHandlerKey, ResourcePermissionHandler<?>> handlers) {
        super(handlers);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.GRADE;
    }
}
