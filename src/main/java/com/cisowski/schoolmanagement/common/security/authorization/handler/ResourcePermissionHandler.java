package com.cisowski.schoolmanagement.common.security.authorization.handler;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;

public interface ResourcePermissionHandler<T> {
    UserType getSupportedUserType();
    ResourceType getSupportedResourceType();
    boolean canAccess(PermissionContext context, ResourceAccessContext accessContext);
}
