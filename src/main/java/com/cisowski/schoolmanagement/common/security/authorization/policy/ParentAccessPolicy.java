package com.cisowski.schoolmanagement.common.security.authorization.policy;

import com.cisowski.schoolmanagement.common.security.authorization.handler.ResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionHandlerKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("PARENT")
public class ParentAccessPolicy extends BaseResourceAccessPolicy<ParentEntity> {

    public ParentAccessPolicy(Map<PermissionHandlerKey, ResourcePermissionHandler<?>> handlers) {
        super(handlers);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.PARENT;
    }
}
