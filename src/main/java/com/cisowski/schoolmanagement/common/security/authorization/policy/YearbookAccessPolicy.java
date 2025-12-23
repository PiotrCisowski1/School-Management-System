package com.cisowski.schoolmanagement.common.security.authorization.policy;

import com.cisowski.schoolmanagement.common.security.authorization.handler.ResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionHandlerKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("YEARBOOK")
public class YearbookAccessPolicy extends BaseResourceAccessPolicy<YearbookEntity> {

    public YearbookAccessPolicy(Map<PermissionHandlerKey, ResourcePermissionHandler<?>> handlers) {
        super(handlers);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.YEARBOOK;
    }
}
